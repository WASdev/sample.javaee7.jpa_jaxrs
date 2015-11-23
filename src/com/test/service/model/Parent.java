/**
* (C) Copyright IBM Corporation 2015.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.test.service.model;

import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Parent element
 */
@XmlRootElement
public class Parent extends Element {
	
	/** 
	 * Hold a list of names of the minions referenced by this element
	 */
	private Set<String> minionNames;
	
	/**
	 * Default constructor
	 */
	public Parent() {
		minionNames = new HashSet<String>();		
	}
	
	/** 
	 * Get the minion names
	 * @return a Set of the minion names
	 */
	public Set<String> getMinionNames() {
		return minionNames;
	}
	
	/**
	 * Set the names of the minions for this parent: should never be populated
	 * by a user, but the method is needed so that it can be populated based
	 * on DB contents
	 * @param minions set of minion names
	 */
	public void setMinionNames(Set<String> minions) {
		minionNames = minions;
	}
	
}
