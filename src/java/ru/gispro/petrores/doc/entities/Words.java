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
public class Words {
    private List<Word> _list;
    private Long _count;

    public Words() {
    }

    public Words(List<Word> list, Long count) {
        _list = list;
        _count = count;
    }

    @XmlElement
    public Long getTotal() {
        return _count;
    }

    @XmlElement
    public List<Word> getWords() {
        return _list;
    }
}
