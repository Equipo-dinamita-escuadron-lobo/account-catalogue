package com.account_catalogue.infraestructure.adapters.output.jpaAdapter.mapper.Impl;

import com.account_catalogue.domain.dto.*;
import com.account_catalogue.domain.models.NodeAccount;
import com.account_catalogue.infraestructure.adapters.output.jpaAdapter.projection.IAccountCatalogueInfoProjection;

import java.util.ArrayList;
import java.util.List;

public class AccountCatalogueTreeInfoMapper {
    public static ClassDTO traverseTree(NodeAccount<IAccountCatalogueInfoProjection> root) {
        if (root == null) {
            return null;
        }

        ClassDTO classDTO = buildClassDTO(root.getData());
        List<GrupDTO> grupDTOs = new ArrayList<>();

        for (NodeAccount<IAccountCatalogueInfoProjection> child : root.getChildren()) {
            grupDTOs.add(traverseGrupNode(child));
        }

        classDTO.setGrupo(grupDTOs);
        return classDTO;
    }

    private static GrupDTO traverseGrupNode(NodeAccount<IAccountCatalogueInfoProjection> node) {
        GrupDTO grupDTO = buildGrupDTO(node.getData());
        List<AccountDTO> accountDTOs = new ArrayList<>();

        for (NodeAccount<IAccountCatalogueInfoProjection> child : node.getChildren()) {
            accountDTOs.add(traverseAccountNode(child));
        }

        grupDTO.setCuenta(accountDTOs);
        return grupDTO;
    }

    private static AccountDTO traverseAccountNode(NodeAccount<IAccountCatalogueInfoProjection> node) {
        AccountDTO accountDTO = buildAccountDTO(node.getData());
        List<SubAccountDTO> subAccountDTOs = new ArrayList<>();

        for (NodeAccount<IAccountCatalogueInfoProjection> child : node.getChildren()) {
            subAccountDTOs.add(traverseSubAccountNode(child));
        }

        accountDTO.setSubcuenta(subAccountDTOs);
        return accountDTO;
    }

    private static SubAccountDTO traverseSubAccountNode(NodeAccount<IAccountCatalogueInfoProjection> node) {
        SubAccountDTO subAccountDTO = buildSubAccountDTO(node.getData());
        List<AncillaryDTO> ancillaryDTOs = new ArrayList<>();

        for (NodeAccount<IAccountCatalogueInfoProjection> child : node.getChildren()) {
            ancillaryDTOs.add(buildAncillaryDTO(child.getData()));
        }

        subAccountDTO.setAuxiliar(ancillaryDTOs);
        return subAccountDTO;
    }

    private static ClassDTO buildClassDTO(IAccountCatalogueInfoProjection data) {
        return ClassDTO.builder()
                .id(data.getId())
                .code(data.getCode())
                .description(data.getDescription())
                .build();
    }

    private static GrupDTO buildGrupDTO(IAccountCatalogueInfoProjection data) {
        return GrupDTO.builder()
                .id(data.getId())
                .code(data.getCode())
                .description(data.getDescription())
                .build();
    }

    private static AccountDTO buildAccountDTO(IAccountCatalogueInfoProjection data) {
        return AccountDTO.builder()
                .id(data.getId())
                .code(data.getCode())
                .description(data.getDescription())
                .build();
    }

    private static SubAccountDTO buildSubAccountDTO(IAccountCatalogueInfoProjection data) {
        return SubAccountDTO.builder()
                .id(data.getId())
                .code(data.getCode())
                .description(data.getDescription())
                .build();
    }

    private static AncillaryDTO buildAncillaryDTO(IAccountCatalogueInfoProjection data) {
        return AncillaryDTO.builder()
                .id(data.getId())
                .code(data.getCode())
                .description(data.getDescription())
                .build();
    }
}
