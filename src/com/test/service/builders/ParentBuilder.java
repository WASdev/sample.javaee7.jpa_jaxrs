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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.test.service.jpa.MinionEntity;
import com.test.service.jpa.ParentEntity;
import com.test.service.model.Parent;

/**
 * Class for building parent database entities from elements and vice versa
 */
public class ParentBuilder extends Builder<ParentEntity, Parent> {
	
	/** Default constructor */
	public ParentBuilder() {
	}

	/**
	 * Build a parentEntity based on an incoming parent element
	 * @param parent with incoming (user) data
	 * @return the entity object populated with the same parameters
	 * @throws IllegalArgumentException if the name is not set
	 */
	public ParentEntity createEntityFromElement(Parent parent) throws IllegalArgumentException{
		ParentEntity pe = new ParentEntity();

		// Call the superclass to populate common parameters (name and description)
		pe = super.populateEntityFromElement(pe, parent);
		
		// Parent has no more properties, so we're done
		
		return pe;
	}
	
	/**
	 * Build a parent element based on the DB contents for a parentEntity
	 * No validation done because the DB contents are assumed to be correct
	 * @param pEntity the parent DB entity 
	 * @return a populated parent element
	 */
	public Parent createElementFromEntity(ParentEntity pEntity) {
		Parent parent = new Parent();
		
		// Call the superclass to populate common parameters (name and description)
		parent = super.populateElementFromEntity(parent, pEntity);
		
		// Get the minions referenced by foreign key
		List<MinionEntity> minions = pEntity.getMinions();
		
		// ... and add their names to the parent element 
		if (minions.size() > 0) {
			Set<String> minionNames = new HashSet<String>();
			
			for (MinionEntity minion: minions) {
				minionNames.add(minion.getName());
			}
			parent.setMinionNames(minionNames);
		}
		
		return parent;
	}	
}
