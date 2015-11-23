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

package com.test.service.builders;

import com.test.service.jpa.DBEntity;
import com.test.service.model.Element;

/** 
 * Class for building database entities from elements and vice versa
 */
public class Builder<D extends DBEntity, E extends Element> {
	
	/**
	 * Default constructor
	 */
	public Builder() {
	}

	/**
	 * Populate a database entity based on the incoming JAX-B element.
	 * Some validation provided, which can produce IllegalArgumentException
	 * @param (blank) entity to be populated
	 * @param element with incoming (user) data
	 * @return populated entity
	 * @throws IllegalArgumentException if there are validation failures
	 */
	public D populateEntityFromElement(D entity, E element) throws IllegalArgumentException {
		
		if (element.getName() == null) {
			throw new IllegalArgumentException("Must provide the name");
		}
		
		entity.setDescription(element.getDescription());
		entity.setName(element.getName());
		
		return entity;
	}
	
	/**
	 * Populate an element based on the contents of the database:
	 * DB contents are assumed to be correct, so no validation here
	 * @param (blank) element to be populated
	 * @param entity database entity with data
	 * @return populated element
	 */
	public E populateElementFromEntity(E element, D entity ) {
		element.setDescription(entity.getDescription());
		element.setName(entity.getName());
		
		return element;
	}	

}
