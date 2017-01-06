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

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Minion element
 */
@XmlRootElement
public class Minion extends Element {
		
	// Name of the parent
	private String parentName;
	// Contents field 
	private ArrayList<String> contents;
	
	/**
	 * Default constructor
	 */
	public Minion() {
		super();
	}
	
	/**
	 * Get the parent
	 * @return name of the parent, can be used to look up the parent in the DB
	 */
	public String getParentName() {
		return parentName;
	}
	
	/**
	 * Set the parent object
	 * @param name of the parent: must match an entity in the DB
	 */
	public void setParentName(String pName) {
		parentName = pName;
	}

	/**
	 * Get the contents
	 * @return Array of contents
	 */
	public ArrayList<String> getContents() {
		return contents;
	}
	
	/** 
	 * Set the contents 
	 * @param The contents to set
	 */
	public void setContents(ArrayList<String> contentsIn) {
		contents = contentsIn;
	}


}
