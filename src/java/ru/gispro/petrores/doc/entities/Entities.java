/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.gispro.petrores.doc.entities;

import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author fkravchenko
 */
@XmlRootElement
public class Entities<T> {

    private int _count;
    private List<T> _entities;

    public Entities() {
    }

    public Entities(List<T> types) {
        this._entities = types;
        this._count = types.size();
    }

    public int getCount() {
        return _count;
    }

    public void setCount(int count) {
        this._count = count;
    }

    @XmlElement
    public List<T> getEntities() {
        return _entities;
    }

    public void setEntities(List<T> entities) {
        this._entities = entities;
        _count = _entities.size();
    }
}
