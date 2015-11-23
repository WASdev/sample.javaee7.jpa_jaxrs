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
import com.test.service.jpa.MinionEntity;
import com.test.service.jpa.ParentEntity;
import com.test.service.model.Minion;

/**
 * Implementation of the RESTful MinionService
 */
public class MinionServiceHelper {
	// Local copy of the DBUtility used to manage interactions with the database
	private DBUtility<MinionEntity> dbUtil;
	// Builder for converting between minion element and DB entity
	private MinionBuilder builder;
	
	/** Default constructor */
    public MinionServiceHelper() {
        dbUtil = new DBUtility<MinionEntity>();
        builder = new MinionBuilder();
    }
    
    /**
     * Get a list of minions matching the name
     * @param name to match, null for all minions
     * @return list of minion elements
     */
 	public List<Minion> get(String name) {
 		// List of minion elements to return
		List<Minion> elements = new ArrayList<Minion>();
		
		// Get the minion entities from the DB
		List<MinionEntity>entities = dbUtil.getEntitiesByName(MinionEntity.TYPE, name);
		
		// Convert each entity to an element
		for(DBEntity entity: entities) {
			MinionEntity mEntity = (MinionEntity) entity;
			
			// Populate the element fields from the entity
			Minion minion = builder.createElementFromEntity(mEntity);
			
			// Process the parent
			if (mEntity.getParent() != null) {
				minion.setParentName(mEntity.getParent().getName());
			}
			// Add the element to the list for return
			elements.add(minion);
		}

		return elements;
	}
	
 	/**
 	 * Delete a minion
 	 * @param name of minion to delete
 	 */
	public void delete(String name) {

		dbUtil.remove(MinionEntity.TYPE, name);
	}
	
	/**
	 * Update minion contents in the DB
	 * @param name of the minion to update
	 * @param newMinion new details of the minion properties
	 * @return the updated minion properties
	 */
	public Minion update (String name, Minion newMinion) {
		
		// Update the DB contents
		MinionEntity mEntity = dbUtil.updateMinion(name, newMinion);
		
		// Convert the returned DB minion entity into a minion element
		Minion minion = builder.createElementFromEntity(mEntity);
		
		return minion;
	}	
	
	/**
	 * Write the contents of a minion to the DB
	 * @param min the new user-populated minion element
	 * @return the minion contents that were written
	 * @throws IllegalArgumentException if a minion with this name is already in the DB, or if a mandatory parameter is missing (name, parentName)
	 */
	public Minion persist(Minion min) {
		String name = min.getName();
		
		if (name == null) {
			throw new IllegalArgumentException("The name parameter is mandatory when creating a Minion");			
		}
		
		// Look up the DB to see if there are any existing minions with this name
		List<MinionEntity> existingMinions = dbUtil.getEntitiesByName(MinionEntity.TYPE, name);
		
		if (existingMinions.size() > 0) {
			throw new IllegalArgumentException("Minion with name " + name + " already exists");
		}
		
		// Convert the incoming minion element into a DB entity
		MinionEntity minionEntity = builder.createEntityFromElement(min);
		
		String pName = min.getParentName();
		
		if (pName == null) {
			throw new IllegalArgumentException("The parentName parameter is mandatory when creating a Minion");
		}
		
		// We need to look up the parent
		DBUtility<ParentEntity> parentUtil = new DBUtility<ParentEntity>();
		
		// Cast is safe because the search is for the parententity type
		//@SuppressWarnings("unchecked")
		List<ParentEntity> parents = (List<ParentEntity>)parentUtil.getEntitiesByName(ParentEntity.TYPE, pName);
		
		if (parents.size() == 0) {
			throw new NotFoundException("Parent not found: " + pName);
		}
		else if (parents.size() == 1) {
			//System.out.println("Setting parent " + pName);
			minionEntity.setParent((ParentEntity)parents.get(0));
		} 
		else {
			throw new IllegalArgumentException("More than one match found for parent " + pName);
		}
		
		// Store the minion object (and related parent)
		dbUtil.persistObject(minionEntity);
		
		return min;
	}
	
}
