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

import javax.persistence.*;

/**
 * JPA class for MinionEntity objects. JPA will automatically create these objects in the database.
 * We haven't specified an inheritance style, the default behaviour is for a single table in Derby
 * with a "dtype" column which records the type of subclass. For more information see:
 * http://docs.oracle.com/javaee/6/tutorial/doc/bnbqn.html
 */
@Entity(name ="Minion")
public class MinionEntity extends DBEntity{
 
	// Used for DB lookups, so needs to match the entity name
	public final static String TYPE = "Minion";
	
	/**
	 * Hold the record of the parent relationship. This JPA resource is calculated
	 * in the JPA layer and you won't find it as a column in the database.
	 * @ManyToOne combined with @JoinColumn means it will be used in to calculate the reverse relationship from the parent
	 * 
	 * Cascade type is ALL: changes to entities in this relationship will be updated in referencing objects
	 * For more details, see https://docs.oracle.com/javaee/7/api/javax/persistence/CascadeType.html
	 */
	@ManyToOne(cascade={CascadeType.REFRESH, CascadeType.MERGE})
	@JoinColumn(name="parentId")
	private ParentEntity parent;

	// Add your data here
	@Column
	private ArrayList<String> contents;
	
	/**
	 * Default constructor
	 */
	public MinionEntity() {
		super();
		contents = new ArrayList<String>();
	}
	
	/**
	 * Parent holds a foreign key in the DB for the relationship with the parent object in the graph config model
	 * One parent can have many children, but each child will only have a single parent, which will be another
	 * metatype in the model
	 * The column is marked as @ManyToOne and @JoinColumn, the DB join is needed to build the reverse relationship
	 */
	public void setParent(ParentEntity parent_in) {
		parent = parent_in;
	}
	
	/**
	 * Get the parent object of this entity
	 * @return the parent
	 */
	public ParentEntity getParent() {
		return parent;
	}
	
	/**
	 * Get the contents
	 * @return array of contents
	 */
	public ArrayList<String> getContents() {
		return contents;
	}
	
	/**
	 * Add content to the contents
	 * @param content
	 */
	public void addContent(String content) {
		contents.add(content);
	}
	
	/**
	 * Set the contents
	 * @param contentsIn an array of contents
	 */
	public void setContents(ArrayList<String> contentsIn) {
		contents = contentsIn;
	}
}