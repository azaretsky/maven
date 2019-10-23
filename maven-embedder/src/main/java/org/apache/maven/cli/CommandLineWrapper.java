package org.apache.maven.cli;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.io.File;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.maven.cli.CLIManager.FileOption;

/**
 * A wrapper around {@link CommandLine}
 * providing a special handling for relative file paths
 *
 * @see FileOption
 */
public class CommandLineWrapper
{
    private final CommandLine commandLine;
    private final Map<FileOption, File> files;

    private CommandLineWrapper( CommandLine commandLine, Map<FileOption, File> files )
    {
        this.commandLine = commandLine;
        this.files = files;
    }

    static CommandLineWrapper wrapCommandLine( String baseDirectory, CommandLine commandLine )
    {
        return new CommandLineWrapper( commandLine, resolveFiles( baseDirectory, commandLine ) );
    }

    static CommandLineWrapper merge( CommandLineWrapper mavenArgs, CommandLineWrapper mavenConfig )
    {
        return new CommandLineWrapper(
                cliMerge( mavenArgs.commandLine, mavenConfig.commandLine ),
                mergeFiles( mavenArgs.files, mavenConfig.files ) );
    }

    private static Map<FileOption, File> resolveFiles( String baseDirectory, CommandLine commandLine )
    {
        Map<FileOption, File> files = new EnumMap<>( FileOption.class );
        for ( FileOption fileOption : FileOption.values() )
        {
            String fileName = commandLine.getOptionValue( fileOption.getOpt() );
            if ( fileName != null )
            {
                files.put( fileOption, ResolveFile.resolveFile( new File( fileName ), baseDirectory ) );
            }
        }
        return files;
    }

    private static CommandLine cliMerge( CommandLine mavenArgs, CommandLine mavenConfig )
    {
        CommandLine.Builder commandLineBuilder = new CommandLine.Builder();

        // the args are easy, cli first then config file
        for ( String arg : mavenArgs.getArgs() )
        {
            commandLineBuilder.addArg( arg );
        }
        for ( String arg : mavenConfig.getArgs() )
        {
            commandLineBuilder.addArg( arg );
        }

        // now add all options, except for -D with cli first then config file
        List<Option> setPropertyOptions = new ArrayList<>();
        for ( Option opt : mavenArgs.getOptions() )
        {
            if ( CLIManager.SET_SYSTEM_PROPERTY.equals( opt.getOpt() ) )
            {
                setPropertyOptions.add( opt );
            }
            else
            {
                commandLineBuilder.addOption( opt );
            }
        }
        for ( Option opt : mavenConfig.getOptions() )
        {
            commandLineBuilder.addOption( opt );
        }
        // finally add the CLI system properties
        for ( Option opt : setPropertyOptions )
        {
            commandLineBuilder.addOption( opt );
        }
        return commandLineBuilder.build();
    }

    private static Map<FileOption, File> mergeFiles( Map<FileOption, File> cliFiles,
                                                     Map<FileOption, File> configFiles )
    {
        Map<FileOption, File> files = new EnumMap<>( cliFiles );
        for ( Map.Entry<FileOption, File> configEntry : configFiles.entrySet() )
        {
            // putIfAbsent is only available since Java 1.8
            if ( !files.containsKey( configEntry.getKey() ) )
            {
                files.put( configEntry.getKey(), configEntry.getValue() );
            }
        }
        return files;
    }

    List<String> getArgList()
    {
        return commandLine.getArgList();
    }

    boolean hasOption( String opt )
    {
        return commandLine.hasOption( opt );
    }

    String getOptionValue( String opt )
    {
        return commandLine.getOptionValue( opt );
    }

    String[] getOptionValues( String opt )
    {
        return commandLine.getOptionValues( opt );
    }

    public File getFile( FileOption fileOption )
    {
        return files.get( fileOption );
    }
}
