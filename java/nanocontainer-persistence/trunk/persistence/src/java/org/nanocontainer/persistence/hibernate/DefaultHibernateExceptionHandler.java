package org.nanocontainer.persistence.hibernate;

import org.hibernate.StaleObjectStateException;
import org.hibernate.TransactionException;
import org.hibernate.UnresolvableObjectException;
import org.hibernate.WrongClassException;
import org.hibernate.exception.LockAcquisitionException;
import org.nanocontainer.persistence.ExceptionFactory;

/**
 * Default Hibernate 3 ExceptionHandler.
 * 
 * @see org.nanocontainer.persistence.hibernate.HibernateExceptionHandler
 * 
 * @version $Id$
 */
public class DefaultHibernateExceptionHandler implements HibernateExceptionHandler {

    private ExceptionFactory exceptionFactory;

    public DefaultHibernateExceptionHandler(ExceptionFactory exceptionFactory) {
        this.exceptionFactory = exceptionFactory;
    }

    public RuntimeException handle(Throwable ex) {
        
        // Optimistic locking cases.
        if (ex instanceof StaleObjectStateException) {
            StaleObjectStateException e = (StaleObjectStateException) ex;
            return exceptionFactory.createStaleObjectStateException(e, e.getEntityName(), e.getIdentifier());
        }

        if (ex instanceof LockAcquisitionException) {
        	LockAcquisitionException e = (LockAcquisitionException) ex;
            return exceptionFactory.createConcurrencyFailureException(e);
        }
        
        // Object retrieval failure cases.
        if (ex instanceof UnresolvableObjectException) {
            UnresolvableObjectException e = (UnresolvableObjectException) ex;
            return exceptionFactory.createObjectRetrievalFailureException(e, e.getEntityName(), e.getIdentifier());
        }

        if (ex instanceof WrongClassException) {
            WrongClassException e = (WrongClassException) ex;
            return exceptionFactory.createObjectRetrievalFailureException(e, e.getEntityName(), e.getIdentifier());
        }

        // Transaction
        if (ex instanceof TransactionException) {
        	return exceptionFactory.createTransactionException(ex); 
        }

        // Otherwise, return an UnknowException kind.
        return exceptionFactory.createDAOException(ex);
    }

}