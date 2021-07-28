package db;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class DatabaseConnection {

    private static DatabaseConnection instance;
    private EntityManagerFactory entityManagerFactory;

    private DatabaseConnection() {
        connect();
    }

    public static DatabaseConnection getInstance(){
        if (instance == null){
            synchronized (DatabaseConnection.class){
                if (instance == null){
                    return new DatabaseConnection();
                }
            }
        }
        return instance;
    }

    public EntityManagerFactory getEntityManagerFactory() {
        if (!entityManagerFactory.isOpen()){
            connect();
        }
        return entityManagerFactory;
    }

    private void connect(){
        entityManagerFactory = Persistence.createEntityManagerFactory("MY-PERSISTENCE-UNIT");
    }

    public void disconnect(){
        if (entityManagerFactory.isOpen()){
            entityManagerFactory.close();
        }
    }


}
