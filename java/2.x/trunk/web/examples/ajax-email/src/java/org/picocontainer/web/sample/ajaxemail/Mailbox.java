package org.picocontainer.web.sample.ajaxemail;

import javax.jdo.Query;
import javax.jdo.PersistenceManager;
import java.util.List;
import java.util.Collection;
import java.util.logging.Logger;

/**
 * Abstract Mailbox
 */
public abstract class Mailbox {

    private final PersistenceManager pm;
    private final User user;
    private final QueryStore queryStore;

    public Mailbox(PersistenceManager pm, User user, QueryStore queryStore) {
        this.pm = pm;
        this.user = user;
        this.queryStore = queryStore;
    }

    protected Message addMessage(Message newMsg) {
        pm.makePersistent(newMsg);
        return newMsg;
    }

    /**
     * Read a message (flip its read flag if not already)
     * @param msgId the message to read
     * @return the message
     */
    public Message read(long msgId) {
        pm.currentTransaction().begin();
        Message message = getMessage(msgId);
        message.markRead();
        pm.currentTransaction().commit();
        return message;
    }

    private Message getMessage(long msgId) {
        Collection<Message> coll = (Collection<Message>) getSingleMessageQuery().execute(msgId);
        if (coll != null && coll.size() == 1) {
            Message message = coll.iterator().next();
            checkUser(message);
            return message;
        }
        throw new AjaxEmailException("no such message ID");
    }

    protected abstract void checkUser(Message message) ;

    protected void throwNotForThisUser() {
        throw new AjaxEmailException("email ID not for the user logged in");
    }

    private Query getSingleMessageQuery() {
        String key = "SM_" + fromOrTo();
        Query query = queryStore.get(key);
        if (query == null) {
            query = pm.newQuery(Message.class, "id == message_id");
            query.declareImports("import java.lang.Long");
            query.declareParameters("Long message_id");
            queryStore.put(key, query);
        }
        return query;
    }

    /**
     * Delete a message
     * @param msgId the message to delete
     */
    public void delete(long msgId) {
        Message message = getMessage(msgId);
        pm.deletePersistent(message);
    }

    /**
     * List the messages for the user
     * @return the messages
     */
    public Message[] messages() {
        Query query = getMultipleMessageQuery();
        List<Message> messageCollection = (List<Message>) query.execute(user.getName());
        return messageCollection.toArray(new Message[messageCollection.size()]);
    }

    private Query getMultipleMessageQuery() {
        String key = "MM_" + fromOrTo();
        Query query = queryStore.get(key);
        if (query == null) {
            query = pm.newQuery(Message.class, fromOrTo() + " == user_name");
            query.declareImports("import java.lang.String");
            query.declareParameters("String user_name");
            queryStore.put(key, query);
        }
        return query;
    }

    protected abstract String fromOrTo();

    protected String getUserName() {
        return user.getName();
    }

}
