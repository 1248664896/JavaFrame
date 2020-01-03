package io.jpress.service.provider;

import com.jfinal.aop.Inject;
import io.jboot.aop.annotation.Bean;
import io.jboot.db.model.Column;
import io.jboot.db.model.Columns;
import io.jboot.service.JbootServiceBase;
import io.jpress.model.Member;
import io.jpress.service.MemberGroupService;
import io.jpress.service.MemberService;
import io.jpress.service.UserService;

import java.util.List;

@Bean
public class MemberServiceProvider extends JbootServiceBase<Member> implements MemberService {

    @Inject
    private MemberGroupService groupService;

    @Inject
    private UserService userService;

    @Override
    public List<Member> findListByUserId(Object userId) {
        List<Member> list = DAO.findListByColumn(Column.create("user_id",userId));
        return userService.join(groupService.join(list,"group_id","group"),"user_id");
    }

    @Override
    public Member findByGroupIdAndUserId(Long groupId, Long userId) {
        return DAO.findFirstByColumns(Columns.create("group_id",groupId).eq("user_id",userId));
    }
}