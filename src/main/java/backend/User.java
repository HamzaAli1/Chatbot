/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package backend;

import java.io.Serializable;

/**
 *
 * @author Hamza Ali
 */
public class User implements Comparable, Serializable {
    private String name;
    
    public User(String n) {
        name = n;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String n) {
        name = n;
    }

    @Override
    public int compareTo(Object o) {
        User other = (User) o;
        if (other.getName().equalsIgnoreCase(name))
            return 0;
        else
            return name.compareTo(other.getName());
    }
    
    @Override
    public String toString() {
        return name;
    }
}
