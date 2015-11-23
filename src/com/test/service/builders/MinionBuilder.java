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

import com.test.service.jpa.MinionEntity;
import com.test.service.jpa.ParentEntity;
import com.test.service.model.Minion;

/** 
 * Class for building minion database entities from elements and vice versa
 */
public class MinionBuilder extends Builder<MinionEntity, Minion> {
	
	/** Default constructor */
	public MinionBuilder() {
	}

	/**
	 * Build a minionEntity based on an incoming minion element: 
	 * does not set the parent because this needs to be done under the same transaction as create
	 * So you will need to set it explicitly after calling this method
	 * @param minion
	 * @return MinionEntity populated except for the parent relationship
	 * @throws IllegalArgumentException
	 */
	public MinionEntity createEntityFromElement(Minion minion) throws IllegalArgumentException {
		MinionEntity minionEntity = new MinionEntity();
		
		// Call the superclass to populate common parameters (name and description)
		minionEntity = super.populateEntityFromElement(minionEntity, minion);

		// Populate the contents
		minionEntity.setContents(minion.getContents());		
		
		return minionEntity;
	}
	
	/**
	 * Build a minion object based on the contents of the database: 
	 * it's assumed that they are correct, so no validation here
	 * @param mEntity database entity
	 * @return the populated minion
	 */
	public Minion createElementFromEntity(MinionEntity mEntity) {
		Minion minion = new Minion();
		
		// Call the superclass to populate common parameters (name and description)
		minion = super.populateElementFromEntity(minion, mEntity);

		// Populate the contents
		minion.setContents(mEntity.getContents());
		
		// Set the parent name based on the parent relationship from the DB
		ParentEntity pEntity = mEntity.getParent();		
		minion.setParentName(pEntity.getName());
				
		return minion;
	}	
}
