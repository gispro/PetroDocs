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
public class Periodicities {
    private List<Periodicity> _list;
    private Long _count;

    public Periodicities() {
    }

    public Periodicities(List<Periodicity> list, Long count) {
        _list = list;
        _count = count;
    }

    @XmlElement
    public Long getTotal() {
        return _count;
    }

    @XmlElement
    public List<Periodicity> getPeriodicities() {
        return _list;
    }
}
