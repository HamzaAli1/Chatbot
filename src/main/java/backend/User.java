/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package backend;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

/**
 *
 * @author Hamza Ali
 */
public class User implements Comparable, Serializable {
    
    //users name -_-
    private String name;
    
    private final ArrayList<String> interests;
    
    public User(String n) {
        name = n;
        interests = new ArrayList<>();
    }
    
    public void addInterest(String i) {
        interests.add(i);
    }
    
    public ArrayList<String> getInterests() {
        return interests;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String n) {
        name = n;
    }

    //checks if names are the same, else uses string compareTo
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
    
    @Override
    public boolean equals(Object o) {
        return this.compareTo(o) == 0;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + Objects.hashCode(this.name);
        return hash;
    }
}
