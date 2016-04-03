package com.pearson.reviewers;

import com.google.gerrit.reviewdb.client.Account;
import com.google.gerrit.reviewdb.client.AccountGroupMember;
import com.google.gerrit.reviewdb.server.ReviewDb;
import com.google.gerrit.server.account.AccountResolver;
import com.google.gwtorm.server.OrmException;
import com.google.gwtorm.server.ResultSet;
import com.google.gwtorm.server.SchemaFactory;
import com.google.inject.Inject;

import java.util.ArrayList;
import java.util.List;

public class GroupsProvider {
    private AccountResolver accountResolver;
    private SchemaFactory<ReviewDb> schemaFactory;
    private ReviewDb reviewDb;

    @Inject
    public GroupsProvider(
        AccountResolver accountResolver,
        SchemaFactory<ReviewDb> schemaFactory
    ) {
        this.accountResolver = accountResolver;
        this.schemaFactory = schemaFactory;
    }

    List<String> getGroupNamesByEmail(String email) throws OrmException {
        reviewDb = schemaFactory.open();

        List<String> groupNames = new ArrayList<>();

        for (AccountGroupMember group : getGroupsByEmail(email)) {
            groupNames.add(getGroupName(group));
        }

        return groupNames;
    }

    private ResultSet<AccountGroupMember> getGroupsByEmail(String email) throws OrmException {
        Account uploaderAccount = accountResolver.findByNameOrEmail(email);
        return reviewDb.accountGroupMembers().byAccount(uploaderAccount.getId());
    }

    private String getGroupName(AccountGroupMember group) throws OrmException {
        return reviewDb.accountGroups().get(group.getAccountGroupId()).getName();
    }
}
