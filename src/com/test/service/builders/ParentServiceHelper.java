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

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.NotFoundException;

import com.test.service.jpa.DBEntity;
import com.test.service.jpa.ParentEntity;
import com.test.service.model.Parent;

/**
 * Implementation of the RESTful ParentService
 */
public class ParentServiceHelper {
	// Local copy of the DBUtility used to manage interactions with the database
	private DBUtility<ParentEntity> dbUtil;
	// Builder for converting between parent element and DB entity
	private ParentBuilder builder;
	
	/** Default constructor */
    public ParentServiceHelper() {
        dbUtil = new DBUtility<ParentEntity>();
        builder = new ParentBuilder();
    }
    
    /**
     * Get a list of parents matching the name
     * @param name to match, null for all parents
     * @return list of minion elements
     */    
	public List<Parent> get(String name) {

		// List of parent elements to return
		List<Parent> elements = new ArrayList<Parent>();
		
		// Get the DB entities matching the name
		List<ParentEntity> entities = dbUtil.getEntitiesByName(ParentEntity.TYPE, name);
		
		// Convert each entity to an element
		for(DBEntity entity: entities) {
			ParentEntity pEntity = (ParentEntity) entity;
			Parent parent = builder.createElementFromEntity(pEntity);
			
			// Add the element to the list for return
			elements.add(parent);
		}
		
		return elements;
	}
		
	/**
	 * Delete a parent
	 * @param name of parent to delete
	 */
	public void delete(String name) {

		dbUtil.remove(ParentEntity.TYPE, name);
	}
	
	/**
	 * Update the parent contents in the DB
	 * @param name of the existing entity
	 * @param newParent new contents to update
	 * @return the updated element
	 * @throws NotFoundException if a Parent with the name is not found
	 *  
	 */
	public Parent update (String name, Parent newParent) throws IllegalArgumentException, NotFoundException {
		// Update the contents
		ParentEntity pEntity = dbUtil.updateParent(name, newParent);
		
		// Convert the returned Entity to an element
		Parent parent = builder.createElementFromEntity(pEntity);
		
		return parent;
	}
	
	/**
	 * Write the contents of a parent to the DB
	 * @param parent the new user-populated parent element
	 * @return the parent contents that were written
	 * @throws IllegalArgumentException if a parent with this name is already in the DB, or if a mandatory parameter is missing (name)
	 */
	public Parent persist(Parent parent) throws IllegalArgumentException {
		
		// Check the DB in case there is already a parent with this name
		List<ParentEntity> existingEntities = dbUtil.getEntitiesByName(ParentEntity.TYPE, parent.getName());
		if (existingEntities.size() > 0) {
			throw new IllegalArgumentException("Parent with name " + parent.getName() + " already exists");
		}
		
		// Convert the incoming parent to an entity object
		ParentEntity pe = builder.createEntityFromElement(parent);
		
		// Persist the entity
		dbUtil.persistObject(pe);
		
		return parent;
	}
}
