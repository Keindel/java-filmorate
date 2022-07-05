package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Objects;

@Data
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Genre implements Comparable<Genre>{
    @EqualsAndHashCode.Include
    private int id;
    private String name;

    @Override
    public int compareTo(Genre obj)
    {
        return (this.id - obj.id);
    }
}
