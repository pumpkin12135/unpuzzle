package com.pumpkin.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.pumpkin.common.lang.Result;
import com.pumpkin.entity.Puzzle;
import com.pumpkin.entity.User;
import com.pumpkin.service.PuzzleService;
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
@RequestMapping("/puzzle")
public class PuzzleController {

    @Autowired
    PuzzleService puzzleService;

    @GetMapping("/level/{id}")
    public Result level(@PathVariable("id") int id){   //从数据库获取昵称，如果获取失败，则添加
        Puzzle puzzle = puzzleService.getById(id);
        return Result.succ(puzzle);
    }

}
