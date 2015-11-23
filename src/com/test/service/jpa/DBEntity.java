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

package com.test.service.jpa;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
/**
 * DBEntity JPA class, holds fields in the DB that are common to all the DB sub-classes
 *
 */
public class DBEntity {
	
	 // Mandatory id, with auto-generated in the JPA layer
	@Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(nullable=false)
	private Integer id;
	
	// Description, free-form text field.
	@Column
	private String description;
	
	// Description, free-form text field.
	@Column
	private String name;
	
	/** 
	 * Get the object description
	 * @return The description string
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * Set the element description, a freeform text field for users to enter a description
	 * @param descr
	 */
	public void setDescription(String descr) {
		description = descr;
	}
	
	/** 
	 * Get the entity name
	 * @return the entity name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Set the entity name
	 * @param the name
	 */
	public void setName(String nameIn) {
		name = nameIn;
	}	
}
