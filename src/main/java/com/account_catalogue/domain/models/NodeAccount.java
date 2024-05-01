package com.account_catalogue.domain.models;

import lombok.*;

import java.util.ArrayList;
import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class NodeAccount<T> {
    private int value;
    private String level;
    private List<NodeAccount<T>> children;
    private T data;

    public NodeAccount(int value, String level, T data) {
        this.value = value;
        this.level = level;
        this.children = new ArrayList<>();
        this.data = data;
    }

    public void addChild(NodeAccount<T> child) {
        children.add(child);
    }
}
