package com.pumpkin.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.pumpkin.common.lang.Result;
import com.pumpkin.entity.User;
import com.pumpkin.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author pumpkin
 * @since 2022-02-19
 */
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    UserService userService;

    @PostMapping("/login")
    public Result login(@Validated @RequestBody User user){   //从数据库获取昵称，如果获取失败，则添加
        User temp;
        temp = userService.getOne(new QueryWrapper<User>().eq("username", user.getUsername()));
        if(temp == null){
            user.setStatus(1);
            userService.save(user);
            return Result.succ(user);
        }
        return Result.succ(temp);
    }

    @PostMapping("/logout")
    public Result logout(@Validated @RequestBody User user){
        userService.saveOrUpdate(user);
        return Result.succ(null);
    }
}
