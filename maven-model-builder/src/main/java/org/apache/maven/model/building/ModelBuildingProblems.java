package org.apache.maven.model.building;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.util.Collection;

/**
 * Collects the problems that are encountered during model building.
 * 
 * @author Benjamin Bentmann
 */
public interface ModelBuildingProblems
{

    /**
     * Adds the specified problem.
     * 
     * @param problem The problem to add, must not be {@code null}.
     */
    void add( ModelProblem problem );

    /**
     * Adds the specified problems.
     * 
     * @param problems The problems to add, must not be {@code null} nor contain {@code null} elements.
     */
    void addAll( Collection<ModelProblem> problems );

}