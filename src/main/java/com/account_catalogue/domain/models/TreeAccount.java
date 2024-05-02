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

public class TreeAccount<T> {
    private NodeAccount<T> root;
    private String[] levelNames = {"clase", "grupo", "cuenta", "subcuenta", "auxiliar"};
    private int[] levelLengths = {1, 2, 4, 6, 8};
    private int currentLevel = 0;
    private List<NodeAccount<T>> currentLevelNodes = new ArrayList<>();

    public void addElement(T element, String code) {
        int codeValue = Integer.parseInt(code);
        int codeLength = code.length();

        if (currentLevel == 0) {
            if (codeLength == levelLengths[currentLevel]) {
                root = new NodeAccount<>(codeValue, levelNames[currentLevel], element);
                currentLevelNodes.add(root);
            } else {

            }
        } else if (currentLevel == 1 && codeLength == levelLengths[currentLevel]) {
            NodeAccount<T> parent = currentLevelNodes.get(0);
            NodeAccount<T> child = new NodeAccount<>(codeValue, levelNames[currentLevel], element);
            parent.addChild(child);
            currentLevelNodes.add(child);
            currentLevelNodes.remove(0);
            if (currentLevelNodes.isEmpty()) {
                currentLevel++;
                currentLevelNodes.addAll(parent.getChildren());
            }
        } else if (currentLevel == 2 && codeLength == levelLengths[currentLevel]) {
            NodeAccount<T> parent = currentLevelNodes.get(0);
            NodeAccount<T> child = new NodeAccount<>(codeValue, levelNames[currentLevel], element);
            parent.addChild(child);
            currentLevelNodes.add(child);
            currentLevelNodes.remove(0);
            if (currentLevelNodes.isEmpty()) {
                currentLevel++;
                currentLevelNodes.addAll(parent.getChildren());
            }
        } else if (currentLevel == 3 && codeLength == levelLengths[currentLevel]) {
            NodeAccount<T> parent = currentLevelNodes.get(0);
            NodeAccount<T> child = new NodeAccount<>(codeValue, levelNames[currentLevel], element);
            parent.addChild(child);
            currentLevelNodes.add(child);
            currentLevelNodes.remove(0);
            if (currentLevelNodes.isEmpty()) {
                currentLevel++;
                currentLevelNodes.addAll(parent.getChildren());
            }
        } else if (currentLevel == 4 && codeLength == levelLengths[currentLevel]) {
            NodeAccount<T> parent = currentLevelNodes.get(0);
            NodeAccount<T> child = new NodeAccount<>(codeValue, levelNames[currentLevel], element);
            parent.addChild(child);
            currentLevelNodes.add(child);
            currentLevelNodes.remove(0);
            if (currentLevelNodes.isEmpty()) {
                currentLevel = 0;
                currentLevelNodes.clear();
            }
        } else {

        }
    }

    public NodeAccount<T> getRoot() {
        return root;
    }

    public void printTree() {
        printTree(root, "");
    }

    private void printTree(NodeAccount<T> node, String indent) {
        if (node != null) {
            System.out.println(indent + node.getLevel() + ": " + node.getValue() + " - " + node.getData());
            for (NodeAccount<T> child : node.getChildren()) {
                printTree(child, indent + "  ");
            }
        }
    }

}
