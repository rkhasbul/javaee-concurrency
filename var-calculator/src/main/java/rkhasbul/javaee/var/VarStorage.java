package rkhasbul.javaee.var;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;

/**
 * Storage for VaR tasks
 * 
 * @author Ruslan Khasbulatov
 * @version 1.0
 */
@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
public class VarStorage {

	private final Map<String, Future<Integer>> storage;

    public VarStorage() {
        this.storage = new HashMap<>();
    }

    @Lock(LockType.WRITE)
    public void put(String key, Future<Integer> task) {
        storage.put(key, task);
    }

    @Lock(LockType.READ)
    public Future<Integer> get(String key) {
        return storage.get(key);
    }
	
}
