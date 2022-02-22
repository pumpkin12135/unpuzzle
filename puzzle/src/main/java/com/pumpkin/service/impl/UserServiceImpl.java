package com.pumpkin.service.impl;

import com.pumpkin.entity.User;
import com.pumpkin.mapper.UserMapper;
import com.pumpkin.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author pumpkin
 * @since 2022-02-19
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

}
