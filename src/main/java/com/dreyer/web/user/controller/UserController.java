package com.dreyer.web.user.controller;

import com.dreyer.common.enums.PublicEnum;
import com.dreyer.common.page.PageParam;
import com.dreyer.common.page.Pager;
import com.dreyer.common.util.StringUtil;
import com.dreyer.common.web.result.ApiResult;
import com.dreyer.common.web.result.ApiResultGenerator;
import com.dreyer.facade.user.entity.User;
import com.dreyer.facade.user.service.UserFacade;
import com.dreyer.facade.user.vo.UserVo;
import com.dreyer.web.common.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @description 用户请求控制器
 * @author: Dreyer
 * @date: 16/3/19 下午7:38
 */
@Controller
@RequestMapping("/user")
public class UserController extends BaseController {
    @Autowired
    private UserFacade userFacade;

    /**
     * 添加用户信息
     *
     * @param request
     */
    @RequestMapping(value = "/", method = {RequestMethod.POST})
    @ResponseBody
    public ApiResult saveUser(HttpServletRequest request) {
        String userName = ServletRequestUtils.getStringParameter(request, "userName", "");
        String password = ServletRequestUtils.getStringParameter(request, "password", "");
        String displayName = ServletRequestUtils.getStringParameter(request, "displayName", "");
        if (StringUtil.isEmpty(userName)) {
            return ApiResultGenerator.badParameter("用户名不能为空!");
        }
        if (StringUtil.isEmpty(password)) {
            return ApiResultGenerator.badParameter("密码不能为空!");
        }
        if (StringUtil.isEmpty(displayName)) {
            return ApiResultGenerator.badParameter("用户展示名不能为空!");
        }
        User user = new User();
        user.setUserName(userName);
        user.setPassowrd(password);
        user.setDisplayName(displayName);
        user.setCreatTime(new Date());
        user.setUpdateTime(new Date());
        user.setIsDelete(PublicEnum.NO.name());

        userFacade.save(user);
        return ApiResultGenerator.success(null);
    }

    /**
     * 删除用户
     *
     * @param request
     * @param id      用户ID
     * @return
     */
    @RequestMapping(value = "/{id}", method = {RequestMethod.DELETE})
    @ResponseBody
    public ApiResult deleteUser(HttpServletRequest request, @PathVariable("id") long id) {
        userFacade.deleteById(id);

        return ApiResultGenerator.success(null);
    }


    /**
     * 修改用户信息
     *
     * @param request
     * @param id
     * @return
     */
    @RequestMapping(value = "/{id}", method = {RequestMethod.PUT})
    @ResponseBody
    public ApiResult updateUser(HttpServletRequest request, @PathVariable("id") long id) {
        String userName = ServletRequestUtils.getStringParameter(request, "userName", "");
        String password = ServletRequestUtils.getStringParameter(request, "password", "");
        String displayName = ServletRequestUtils.getStringParameter(request, "displayName", "");
        if (StringUtil.hasEmpty(userName, password, displayName)) {
            return ApiResultGenerator.badParameter("userName && password && displayName 不允许为空!");
        }
        User user = userFacade.findById(id);
        user.setUserName(userName);
        user.setDisplayName(displayName);
        user.setPassowrd(password);
        user.setUpdateTime(new Date());

        userFacade.update(user);

        return ApiResultGenerator.success(null);
    }

    /**
     * 查询单个用户信息
     *
     * @param request
     * @param id
     * @return
     */
    @RequestMapping(value = "/{id}", method = {RequestMethod.GET})
    @ResponseBody
    public ApiResult findUserById(HttpServletRequest request, @PathVariable("id") long id) {
        User user = userFacade.findById(id);

        return ApiResultGenerator.success(UserVo.build(user));
    }


    /**
     * 模拟用户登录
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/login", method = {RequestMethod.POST})
    @ResponseBody
    public ApiResult login(HttpServletRequest request) {
        String userName = ServletRequestUtils.getStringParameter(request, "userName", "");
        String passWord = ServletRequestUtils.getStringParameter(request, "passWord", "");
        String ip = getIpAddr(request);
        boolean flag = userFacade.login(userName, passWord, ip);
        if (flag) {
            return ApiResultGenerator.success(null, "登录成功!");
        }
        return ApiResultGenerator.fail("用户名/密码错误!");
    }

    /**
     * 用户列表分页
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/list", method = {RequestMethod.GET})
    @ResponseBody
    public ApiResult listUser(HttpServletRequest request) {
        int pageSize = ServletRequestUtils.getIntParameter(request, "pageSize", 10);
        int currentPageIndex = ServletRequestUtils.getIntParameter(request, "currentPageIndex", 0);
        String order = ServletRequestUtils.getStringParameter(request, "order", "");
        String sort = ServletRequestUtils.getStringParameter(request, "sort", "");

        String userName = ServletRequestUtils.getStringParameter(request, "userName", "");
        Map<String, Object> param = new HashMap<String, Object>();
        if (StringUtil.isNotEmpty(userName)) {
            param.put("userName", userName);
        }
        PageParam pageParam = new PageParam(pageSize, currentPageIndex, sort, order);

        Pager<UserVo> userPager = userFacade.list(param, pageParam);

        return ApiResultGenerator.success(userPager);
    }


}
