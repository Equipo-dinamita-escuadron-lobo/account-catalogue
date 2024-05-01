package com.account_catalogue.infraestructure.adapters.output.jpaAdapter;

import com.account_catalogue.application.output.IAccountCatalogueSearchOutputPort;
import com.account_catalogue.domain.dto.AccountCatalogueInfoDTO;
import com.account_catalogue.domain.dto.ClassDTO;
import com.account_catalogue.domain.dto.InformationDTO;
import com.account_catalogue.domain.models.AccountCatalogue;
import com.account_catalogue.domain.models.NodeAccount;
import com.account_catalogue.domain.models.TreeAccount;
import com.account_catalogue.infraestructure.adapters.output.jpaAdapter.entity.AccountCatalogueEntity;
import com.account_catalogue.infraestructure.adapters.output.jpaAdapter.mapper.IAccountCatalogueSearchMapper;
import com.account_catalogue.infraestructure.adapters.output.jpaAdapter.mapper.IItemAccountCatalogueSearchMapper;
import com.account_catalogue.infraestructure.adapters.output.jpaAdapter.mapper.Impl.AccountCatalogueTreeInfoMapper;
import com.account_catalogue.infraestructure.adapters.output.jpaAdapter.projection.IAccountCatalogueInfoProjection;
import com.account_catalogue.infraestructure.adapters.output.jpaAdapter.repository.IAccountCatalogueRepository;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Data
public class AccountCatalogueSearchJpaAdapter implements IAccountCatalogueSearchOutputPort {
    private final IAccountCatalogueRepository accountCatalogueRepository;
    private final IAccountCatalogueSearchMapper accountCatalogueSearchMapper;
    private final IItemAccountCatalogueSearchMapper itemAccountCatalogueSearchMapper;
    private  AccountCatalogueTreeInfoMapper accountCatalogueTreeInfoMapper;
    @Override
    public List<AccountCatalogueInfoDTO> getAllAccountCatalogue(String code) {
        List<IAccountCatalogueInfoProjection> accountCatalogue=accountCatalogueRepository.getAllAccountCatalogue(code);

        List<IAccountCatalogueInfoProjection> accountCatalogueOrdenado = accountCatalogue.stream()
                .sorted(Comparator.comparingInt(item -> Integer.parseInt(item.getCode())))
                .collect(Collectors.toList());

        TreeAccount treeAccount=new TreeAccount();
        
        for(IAccountCatalogueInfoProjection item: accountCatalogueOrdenado){
            treeAccount.addElement(item, item.getCode());
        }
        treeAccount.printTree();

        NodeAccount<IAccountCatalogueInfoProjection> rootNode = treeAccount.getRoot();
        ClassDTO classDTO = accountCatalogueTreeInfoMapper.traverseTree(rootNode);

        return accountCatalogueSearchMapper.toModelListAccountCatalogue(accountCatalogue);
    }

    @Override
    public AccountCatalogue getAccountCatalogueByCode(String code) {
        AccountCatalogueEntity accountCatalogue=accountCatalogueRepository.findByCode(code);
        return itemAccountCatalogueSearchMapper.toDomain(accountCatalogue);
    }
}
