package org.caesar.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.caesar.model.entity.Menu;

import java.util.List;

/**
* @author caesar
* @description 针对表【sys_menu】的数据库操作Mapper
* @createDate 2023-05-01 19:26:18
* @Entity org.caesar.model.entity.Menu
*/
@Mapper
public interface MenuMapper extends BaseMapper<Menu> {

    List<String> selectPermsByUserId(long userId);

    List<Integer> selectRolesByUserId(long userId);

    List<String> selectPermsByRoleId(long roleId);
}




