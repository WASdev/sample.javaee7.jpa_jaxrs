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

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

@Entity(name ="Parent")
public class ParentEntity extends DBEntity {

	/** Used for DB lookups, so needs to match the entity name */
	public final static String TYPE = "Parent";

	/**
	 * Reference to the children, mapped by a reverse operation on the parent relationship. This JPA resource is calculated
	 * in the JPA layer and you won't find it as a column in the database. There is also no setter for it, because it's 
	 * automatically calculated
	 */
	@OneToMany(targetEntity=MinionEntity.class, mappedBy="parent", cascade=CascadeType.ALL)
	private List<MinionEntity> minions;
	
	public ParentEntity() {
		super();
	}
	
	/**
	 * get the list of minions for this parent
	 * @return List of MinionEntity objects
	 */
	public List<MinionEntity> getMinions() {
		if (minions == null) {
			minions = new ArrayList<MinionEntity>();
		}
		return minions;
	}
}