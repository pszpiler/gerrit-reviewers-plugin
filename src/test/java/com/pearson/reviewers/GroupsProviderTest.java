package com.pearson.reviewers;

import com.google.gerrit.reviewdb.client.Account;
import com.google.gerrit.reviewdb.client.AccountGroup;
import com.google.gerrit.reviewdb.client.AccountGroup.Id;
import com.google.gerrit.reviewdb.client.AccountGroupMember;
import com.google.gerrit.reviewdb.server.AccountGroupAccess;
import com.google.gerrit.reviewdb.server.AccountGroupMemberAccess;
import com.google.gerrit.reviewdb.server.ReviewDb;
import com.google.gerrit.server.account.AccountResolver;
import com.google.gwtorm.server.ListResultSet;
import com.google.gwtorm.server.OrmException;
import com.google.gwtorm.server.SchemaFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.mockito.Mockito.*;

public class GroupsProviderTest {

    private static String UPLOADER_EMAIL = "test@test.com";

    private AccountResolver accountResolverMock;
    private SchemaFactory<ReviewDb> schemaFactoryMock;
    private ReviewDb reviewDbMock;
    private AccountGroupMemberAccess accountGroupMemberAccessMock;
    private Account.Id accountIdMock;
    private Account account;
    private List<AccountGroupMember> groupMembers;
    private ListResultSet<AccountGroupMember> listResultSet;

    private GroupsProvider sut;

    @Before
    public void setUp() throws OrmException {
        accountResolverMock = Mockito.mock(AccountResolver.class);
        schemaFactoryMock = (SchemaFactory<ReviewDb>)Mockito.mock(SchemaFactory.class);
        reviewDbMock = Mockito.mock(ReviewDb.class);
        accountGroupMemberAccessMock = Mockito.mock(AccountGroupMemberAccess.class);
        accountIdMock = Mockito.mock(Account.Id.class);
        account = new Account(accountIdMock, Mockito.mock(Timestamp.class));
        groupMembers = new ArrayList<>();
        listResultSet = new ListResultSet<>(groupMembers);

        when(schemaFactoryMock.open()).thenReturn(reviewDbMock);
        when(accountResolverMock.findByNameOrEmail(UPLOADER_EMAIL)).thenReturn(account);
        when(reviewDbMock.accountGroupMembers()).thenReturn(accountGroupMemberAccessMock);
        when(accountGroupMemberAccessMock.byAccount(accountIdMock)).thenReturn(listResultSet);

        sut = new GroupsProvider(accountResolverMock, schemaFactoryMock);
    }

    @Test
    public void shouldReturnEmptyGroupListIfUploaderDoNotBelongToAnyGroup()
        throws OrmException {
        // when
        List<String> result = sut.getGroupNamesByEmail(UPLOADER_EMAIL);

        // then
        assertTrue(result.isEmpty());
        verify(reviewDbMock, never()).accountGroups();
    }

    @Test
    public void shouldReturnGroupListToWhichTheUploaderBelongs()
        throws OrmException {

        // given
        String groupName01 = "group-01";
        String groupName02 = "group-02";

        Id accountGroupId01 = Mockito.mock(Id.class);
        Id accountGroupId02 = Mockito.mock(Id.class);

        AccountGroupMember.Key key01 = new AccountGroupMember.Key(accountIdMock, accountGroupId01);
        AccountGroupMember.Key key02 = new AccountGroupMember.Key(accountIdMock, accountGroupId02);

        AccountGroupMember group01 = new AccountGroupMember(key01);
        AccountGroupMember group02 = new AccountGroupMember(key02);

        AccountGroup.NameKey accountGroupName01 = Mockito.mock(AccountGroup.NameKey.class);
        AccountGroup accountGroup01 = new AccountGroup(
            accountGroupName01, accountGroupId01, Mockito.mock(AccountGroup.UUID.class)
        );

        AccountGroup.NameKey accountGroupName02 = Mockito.mock(AccountGroup.NameKey.class);
        AccountGroup accountGroup02 = new AccountGroup(
            accountGroupName02, accountGroupId02, Mockito.mock(AccountGroup.UUID.class)
        );

        AccountGroupAccess accountGroupAccessMock = Mockito.mock(AccountGroupAccess.class);

        when(reviewDbMock.accountGroups()).thenReturn(accountGroupAccessMock);

        when(accountGroupAccessMock.get(accountGroupId01)).thenReturn(accountGroup01);
        when(accountGroupName01.get()).thenReturn(groupName01);

        when(accountGroupAccessMock.get(accountGroupId02)).thenReturn(accountGroup02);
        when(accountGroupName02.get()).thenReturn(groupName02);

        groupMembers.add(group01);
        groupMembers.add(group02);

        // when
        List<String> result = sut.getGroupNamesByEmail(UPLOADER_EMAIL);

        // then
        assertEquals(2, result.size());
        assertEquals(groupName01, result.get(0));
        assertEquals(groupName02, result.get(1));
        verify(schemaFactoryMock, times(1)).open();
        verify(reviewDbMock, times(1)).close();
    }
}
