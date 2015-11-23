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

import java.util.List;

import javax.annotation.Resource;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.transaction.UserTransaction;
import javax.ws.rs.NotFoundException;

import com.test.service.jpa.DBEntity;
import com.test.service.jpa.MinionEntity;
import com.test.service.jpa.ParentEntity;
import com.test.service.model.Minion;
import com.test.service.model.Parent;

/**
 * 
 * Class which manages the interactions with the database: makes extensive use of the DBEntity class
 * and its inheriting subclasses
 */
public class DBUtility<E extends DBEntity> {
	
	// The start of a SELECT statement. Remember to append a type and the QUERY_VAR 
	private static final String TYPE_QUERY = "SELECT x from ";
	// Necessary closing on the SELECT statement 
	private static final String QUERY_VAR = " x";
	// Optional WHERE clause for name 
	private static final String NAME_QUERY = " WHERE x.name = :name";	
	// Local entity manager 
	private EntityManager em;
	
	// Transaction for DB operations, populated via the JPA EntityManager
	@Resource
	private UserTransaction userTran;
	
	// initial JNDI context
	private InitialContext ctx;
	
	/** 
	 * JNDI name of the DB persistence : must match the persistence-context-ref-name in web.xml 
	 * (with java:comp/env prefix added for Liberty)
	 * @see getEntityManager()
	 */
	public static final String JNDI_NAME = "java:comp/env/TestServicePC";
	
	/**
	 * Default constructor
	 */
	public DBUtility() {
		initEntityManager();
	}
	
	/**
	 * Local helper method to populate an entity manager and connect to the database
	 * No user transaction is provided
	 * The entity manager is a global variable
	 */
	private void initEntityManager() {
		
		try {
			// Initialise the context
			ctx = new InitialContext();
			// Needed for Derby
			em = (EntityManager) ctx.lookup(JNDI_NAME);			
		}
		catch (NamingException ne) {
			System.out.println("getEntityManager naming error " + ne.getMessage());			
		}
		
		if (em == null) {
			System.out.println("ERROR failed to initialise entity manager");
		}
	}
	
	/**
	 * Local helper method to initialise a transaction and join it to the entity manager
	 * Both entity manager and transaction are global variables
	 * Make sure you call commit() after you have done the transacted work
	 */
	public void getTran() {
		
		try {
			userTran = (UserTransaction) ctx.lookup("java:comp/UserTransaction");
			userTran.begin();
			em.joinTransaction();
		} 
		catch (Exception e) {
			System.out.println("getTran error " + e.getMessage());			
		}
	}
	
	/**
	 * Commit the transaction after work is done
	 * Should always be called after getEntityManagerWithTran()
	 * Should not be called after getEntityManager()
	 */
	public void commit() {
		
		// You must not close container-managed entity managers, so all we need to do here is 
		// commit the transaction (if there was one)
		try {
			if (userTran != null) {
				userTran.commit();
			}
		}
		catch (Exception e) {
			System.out.println("ERROR Commit failed " + e.getMessage() );
		}
	}
	
	/**
	 * Get a List of DB entity objects matching sub-class and name
	 * @param type the name of a sub-class of DBEntity
	 * @param name to match, can be null for all entries of the sub-class type
	 * @return a List of DBEntity objects matching the query
	 */
	public List<E> getEntitiesByName(String type, String name) 
	{
		String queryStr = TYPE_QUERY + type + QUERY_VAR;
		Query query;
		
		// Entity manager must have been initialised
		if (em == null) {
			System.out.println("ERROR Failed to initialise JNDI and JPA");
			return null;
		}
		
		// Null name is allowed: if so, don't query for name
		if (name == null) {
			query = em.createQuery(queryStr);
		}
		else {
			queryStr += NAME_QUERY;
			query = em.createQuery(queryStr);
			query.setParameter("name",  name);
		}
		
		// Run the query
		List<E> queryResults = getEntitiesFromQuery(query);
		
		return queryResults;
	}
	
	/**
	 * Get the entities for a query, and make the cast to a generic DBEntity type
	 * @param query
	 * @return the list of entities, pre-cast to a generic DBEntity type
	 */
	private List<E> getEntitiesFromQuery(Query query) {
		
		// All the database JPA entities extend DBEntity, so the cast is safe
		@SuppressWarnings("unchecked")
		List<E> results = (List<E>) query.getResultList();
		
		return results;
	}

	/** 
	 * Write an object from the model to the database
	 * @param entity the incoming object
	 */
	public void persistObject(DBEntity entity)
	{
		// Initialise the transaction
		getTran();
		
		// Persist the object
		try {
			em.persist(entity);
		}
		catch (Exception e) {
			System.out.println("ERROR Failed to persist entity " + entity.getName());
			System.out.println(e.getMessage());
		}
		// Always try and commit
		finally {
			this.commit();
		}		
	}
	
	/**
	 * Remove (aka delete) all objects of a type from the database under a single transaction
	 * If cascade type is set to REMOVE or ALL on @ManyToOne or @OneToMany (see XXXEntity classes), 
	 * all children of the object, ie those referred to via foreign key, will be deleted too.
	 * Therefore use with care
	 * @param type The type of object to delete, null allowed but will delete ALL the DB contents
	 */
	public void removeAll(String type) {
		System.out.println("removeObjects: will delete ALL DB objects of type " + type + ", and their children");
		
		// Initialise the transaction
		getTran();
	
		if (type == null) {
			System.out.println("WARNING deleting everything!");
		}
		
		// Get a list of the entities: name is null, so will find all entities matching the type
		// If type is also null, this will find everything
		List<E> entities = getEntitiesByName(type, null);
			
		// Delete each one
		try {
			for (DBEntity entity: entities) {
				em.remove(entity);
			}
		}
		catch (Exception e) {
			System.out.println("ERROR in remove");
			System.out.println(e.getMessage());
		}
		// Commit after all the deletes are complete
		finally {
			this.commit();
		}
	}
	
	/**
	 * Remove (aka delete) a single object from the database
	 * @param type The object type
	 * @param name The object name
	 */
	public void remove(String type, String name)
	{	
		// Initialise the transaction
		getTran();
		
		// Get a list of the object(s) that match type and name
		List<E>entities = getEntitiesByName(type, name);
		
		if (entities.size() < 1) {
			System.out.println("WARNING No " + type + " matches in DB with name " + name);
		}
		
		// Delete each one
		try {
			for (DBEntity entity: entities) {	
				em.remove(entity);
			}
		}
		catch (Exception e) {
			System.out.println("removeObject");
			System.out.println(e.getMessage());
			//e.printStackTrace();
		}
		// After all the deletes, commit
		finally {
			this.commit();
		}
	}
	
	/** 
	 * Update the contents of a parent in the DB: type-specific method because we need to be to query and update
	 * under the same transaction, and we need to know about the type-specific fields to update
	 * @param name of the parent to update
	 * @param newParent element with the new contents
	 * @return the updated parent entity now in the DB
	 * @throws IllegalArgumentException if the DB contains more than one object with this name and type, the name should be unique
	 * @throws NotFoundException if the DB does not contain any object with this name and type
	 */
	public ParentEntity updateParent(String name, Parent newParent) throws IllegalArgumentException, NotFoundException {
		// Initialise the transaction
		getTran();
		
		// Cast is safe because we know the type (ParentEntity)
		@SuppressWarnings("unchecked")
		List<ParentEntity> entities = (List<ParentEntity>)getEntitiesByName(ParentEntity.TYPE, name);
		
		if (entities.size() > 1) {
			throw new IllegalArgumentException("More than one parent found with name " + name);
		}
		
		if (entities.size() == 0) {
			throw new NotFoundException("No parent found with name " + name);			
		}
		
		// If we get here, there must be exactly one match - otherwise, we'll have thrown one of the previous two exceptions
		DBEntity parentToUpdate = entities.get(0);

		// If the description has been updated in the incoming object, also update it in DB
		if (newParent.getDescription() != null) {
			parentToUpdate.setDescription(newParent.getDescription());
		}
		
		// If the name has been updated in the incoming object, also update it in DB
		if (newParent.getName() != null) {
			parentToUpdate.setName(newParent.getName());			
		}
		
		// Merge (update) the contents in the DB
		em.merge(parentToUpdate);
	
		// Commit
		commit();
		
		return (ParentEntity)parentToUpdate;
	}

	/** 
	 * Update the contents of a minion in the DB: type-specific method because we need to be to query and update
	 * under the same transaction, and we need to know about the type-specific fields to update
	 * @param name of the minion to update
	 * @param newMinion element with the new contents
	 * @return the updated minion entity now in the DB
	 * @throws IllegalArgumentException if the DB contains more than one object with this name and type, the name should be unique
	 * @throws NotFoundException if the DB does not contain any minion with this name and type, or a parent matching the new parentName
	 */
	
	public MinionEntity updateMinion(String name, Minion newMinion) throws IllegalArgumentException {
		getTran();
		
		// Cast is safe because the type is the same
		@SuppressWarnings("unchecked")
		List<DBEntity> entities = (List<DBEntity>) getEntitiesByName(MinionEntity.TYPE, name);
		
		if (entities.size() > 1) {
			throw new IllegalArgumentException("More than one minion found with name " + name);
		}
		
		if (entities.size() == 0) {
			throw new NotFoundException("No minion found with name " + name);			
		}
		
		// If we get here, there must be exactly one match - otherwise, we'll have thrown one of the previous two exceptions
		MinionEntity minionToUpdate = (MinionEntity)entities.get(0);

		// If the description has been updated in the incoming object, also update it in DB
		if (newMinion.getDescription() != null) {
			minionToUpdate.setDescription(newMinion.getDescription());
		}
		
		// If the name has been updated in the incoming object, also update it in DB
		if (newMinion.getName() != null) {
			minionToUpdate.setName(newMinion.getName());			
		}
		
		// If the contents have been updated in the incoming object, also update them in DB
		if (newMinion.getContents() != null) {
			minionToUpdate.setContents(newMinion.getContents());			
		}
		
		// If the parent has been updated in the incoming object, also update it in DB
		if (newMinion.getParentName() != null) {
			String pName = newMinion.getParentName();
			
			// Check the parent is in the DB: cast is safe because we are searching for the parententity type
			@SuppressWarnings("unchecked")
			List<ParentEntity> parents = (List<ParentEntity>)getEntitiesByName(ParentEntity.TYPE, pName);
			
			if (parents.size() > 1) {
				throw new IllegalArgumentException("More than one parent found with name " + name);
			}
			
			if (parents.size() == 0) {
				throw new NotFoundException("No parent found with name " + name);			
			}
			
			// If we get here, there was exactly one match
			minionToUpdate.setParent((ParentEntity)parents.get(0));
		}
		
		// Update the DB with the new entity contents
		em.merge(minionToUpdate);
	
		// Commit the transaction
		commit();
		
		return minionToUpdate;
	}	
}
