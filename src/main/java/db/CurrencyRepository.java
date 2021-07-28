package db;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class CurrencyRepository {

    private final EntityManager entityManager;

    public CurrencyRepository(DatabaseConnection databaseConnection) {
        this.entityManager = databaseConnection.getEntityManagerFactory().createEntityManager();
    }

    public Optional<Currency> findCurrency(String currencyCode) {
        return Optional.ofNullable(entityManager.find(Currency.class, currencyCode));
    }

    public Optional<List<Currency>> findAllCurrencies() {
        return Optional.ofNullable(entityManager.createQuery("FROM Currency", Currency.class).getResultList());
    }

    public void addCurrency(Currency currency) {
        doInTransaction(currency, entry -> entityManager.persist(currency));
    }

    public void removeCurrency(String currencyCode){
        Optional<Currency> currency = findCurrency(currencyCode);
        if (currency.isPresent()) {
            doInTransaction(currency.get(), entry -> entityManager.remove(currency.get()));
        }
    }

    public void doInTransaction(Currency currency, Consumer<Currency> consumer){
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        consumer.accept(currency);
        transaction.commit();
    }

}
