package org.jeecg.modules.wallplayer.controller;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.apache.commons.lang.StringUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.util.JwtUtil;
import org.jeecg.common.util.IPUtils;
import org.jeecg.common.util.PasswordUtil;
import org.jeecg.common.util.encryption.AesEncryptUtil;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.advertisersoffer.entity.WallAdvertisersOffer;
import org.jeecg.modules.base.service.BaseCommonService;
import org.jeecg.modules.system.entity.SysUser;
import org.jeecg.modules.wallplayer.entity.WallPlayer;
import org.jeecg.modules.wallplayer.service.IWallPlayerService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecg.common.system.base.controller.JeecgController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.jeecg.common.aspect.annotation.AutoLog;

 /**
 * @Description: wall_player
 * @Author: jeecg-boot
 * @Date:   2021-05-23
 * @Version: V1.0
 */
@Api(tags="wall_player")
@RestController
@RequestMapping("/wallplayer/wallPlayer")
@Slf4j
public class WallPlayerController extends JeecgController<WallPlayer, IWallPlayerService> {
	@Autowired
	private IWallPlayerService wallPlayerService;
	 @Resource
	 private BaseCommonService baseCommonService;
	/**
	 * ??????????????????
	 *
	 * @param wallPlayer
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "wall_player-??????????????????")
	@ApiOperation(value="wall_player-??????????????????", notes="wall_player-??????????????????")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(WallPlayer wallPlayer,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		wallPlayer.setDelFlag("0");
		QueryWrapper<WallPlayer> queryWrapper = QueryGenerator.initQueryWrapper(wallPlayer, req.getParameterMap());
		Page<WallPlayer> page = new Page<WallPlayer>(pageNo, pageSize);
		IPage<WallPlayer> pageList = wallPlayerService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	/**
	 *   ??????
	 *
	 * @param wallPlayer
	 * @return
	 */
	@AutoLog(value = "wall_player-??????")
	@ApiOperation(value="wall_player-??????", notes="wall_player-??????")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody WallPlayer wallPlayer, HttpServletRequest request) {
		String ip = IPUtils.getIpAddr(request);
		String ua = request.getHeader("user-agent");
		wallPlayer.setIp(ip);
		wallPlayer.setUa(ua);
		wallPlayer.setDelFlag("0");
		String password =wallPlayer.getPlayerPassword();
		String salt = oConvertUtils.randomGen(8);
		if(password==null||"".equals(StrUtil.nullToEmpty(password))){
			password = PasswordUtil.encrypt(wallPlayer.getPlayerName(), "123456", salt);
		}else {
			password = PasswordUtil.encrypt(wallPlayer.getPlayerName(), wallPlayer.getPlayerPassword(), salt);
		}
		wallPlayer.setSalt(salt);
		wallPlayerService.save(wallPlayer);
		return Result.OK("???????????????");
	}

	/**
	 *  ??????
	 *
	 * @param wallPlayer
	 * @return
	 */
	@AutoLog(value = "wall_player-??????")
	@ApiOperation(value="wall_player-??????", notes="wall_player-??????")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody WallPlayer wallPlayer) {
		wallPlayerService.updateById(wallPlayer);
		return Result.OK("????????????!");
	}

	/**
	 *   ??????id??????
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "wall_player-??????id??????")
	@ApiOperation(value="wall_player-??????id??????", notes="wall_player-??????id??????")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
//		wallPlayerService.removeById(id);
//		return Result.OK("????????????!");
		Result<WallPlayer> result = new Result<WallPlayer>();
		try {
			WallPlayer player = wallPlayerService.getById(id);
			if(player==null) {
				result.error500("?????????????????????");
			}else{
				player.setDelFlag("1");
				wallPlayerService.updateById(player);
				result.success("????????????!");
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			result.error500("????????????");
		}
		return result;
	}

	/**
	 *  ????????????
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "wall_player-????????????")
	@ApiOperation(value="wall_player-????????????", notes="wall_player-????????????")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
//		this.wallPlayerService.removeByIds(Arrays.asList(ids.split(",")));
//		return Result.OK("??????????????????!");
		Result<WallPlayer> result = new Result<WallPlayer>();
		try {
			String[] arr = ids.split(",");
			for (String id : arr) {
				if(oConvertUtils.isNotEmpty(id)) {
					this.wallPlayerService.update(new WallPlayer().setDelFlag("1"),
							new UpdateWrapper<WallPlayer>().lambda().eq(WallPlayer::getId,id));
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			result.error500("????????????"+e.getMessage());
		}
		result.success("??????????????????!");
		return result;
	}
	 /**
	  * ??????&????????????
	  * @param jsonObject
	  * @return
	  */
	 //@RequiresRoles({"admin"})
	 @AutoLog(value = "??????&????????????WallPlayer")
	 @RequestMapping(value = "/frozenBatch", method = RequestMethod.PUT)
	 public Result<WallPlayer> frozenBatch(@RequestBody JSONObject jsonObject) {
		 Result<WallPlayer> result = new Result<WallPlayer>();
		 try {
			 String ids = jsonObject.getString("ids");
			 String status = jsonObject.getString("status");
			 String[] arr = ids.split(",");
			 for (String id : arr) {
				 if(oConvertUtils.isNotEmpty(id)) {
					 this.wallPlayerService.update(new WallPlayer().setStatus(Integer.parseInt(status)),
							 new UpdateWrapper<WallPlayer>().lambda().eq(WallPlayer::getId,id));
				 }
			 }
		 } catch (Exception e) {
			 log.error(e.getMessage(), e);
			 result.error500("????????????"+e.getMessage());
		 }
		 result.success("????????????!");
		 return result;

	 }
	/**
	 * ??????id??????
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "wall_player-??????id??????")
	@ApiOperation(value="wall_player-??????id??????", notes="wall_player-??????id??????")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		WallPlayer wallPlayer = wallPlayerService.getById(id);
//		String password = wallPlayer.getPlayerPassword();
//		String salt = wallPlayer.getSalt();
//		if(password!=null&&!"".equals(StrUtil.nullToEmpty(password))){
//			password = PasswordUtil.decrypt(wallPlayer.getPlayerName(), wallPlayer.getPlayerPassword(), salt);
//		}
//		wallPlayer.setPlayerPassword(password);
		if(wallPlayer==null) {
			return Result.error("?????????????????????");
		}
		return Result.OK(wallPlayer);
	}

    /**
    * ??????excel
    *
    * @param request
    * @param wallPlayer
    */
	@AutoLog(value = "??????wallPlayer??????")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, WallPlayer wallPlayer) {
        return super.exportXls(request, wallPlayer, WallPlayer.class, "wall_player");
    }

    /**
      * ??????excel????????????
    *
    * @param request
    * @param response
    * @return
    */
    @AutoLog(value = "??????excel??????wallPlayer??????")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, WallPlayer.class);
    }
	 /**
	  * ????????????????????????wallPlayer??????????????????
	  *
	  * @return logicDeletedUserList
	  */
	 @GetMapping("/recycleBin")
	 @AutoLog(value = "????????????????????????wallPlayer??????????????????")
	 public Result getRecycleBin() {
		 List<WallPlayer> logicDeletedOfferList = wallPlayerService.queryLogicDeleted();
		 return Result.ok(logicDeletedOfferList);
	 }

	 /**
	  * ????????????????????????wallPlayer
	  *
	  * @param jsonObject
	  * @return
	  */
	 @AutoLog(value = "????????????????????????wallPlayer")
	 @RequestMapping(value = "/putRecycleBin", method = RequestMethod.PUT)
	 public Result putRecycleBin(@RequestBody JSONObject jsonObject, HttpServletRequest request) {
		 String wallPlayerIds = jsonObject.getString("wallPlayerIds");
		 if (StringUtils.isNotBlank(wallPlayerIds)) {
			 WallPlayer updatePlayer = new WallPlayer();
			 updatePlayer.setUpdateBy(JwtUtil.getUserNameByToken(request));
			 updatePlayer.setUpdateTime(new Date());
			 wallPlayerService.revertLogicDeleted(Arrays.asList(wallPlayerIds.split(",")), updatePlayer);
		 }
		 return Result.ok("????????????");
	 }

	 /**
	  * ????????????wallPlayer
	  *
	  * @param wallPlayerIds ????????????offerID?????????id?????????????????????
	  * @return
	  */
	 //@RequiresRoles({"admin"})
	 @AutoLog(value = "????????????wallPlayer")
	 @RequestMapping(value = "/deleteRecycleBin", method = RequestMethod.DELETE)
	 public Result deleteRecycleBin(@RequestParam("wallPlayerIds") String wallPlayerIds) {
		 if (StringUtils.isNotBlank(wallPlayerIds)) {
			 wallPlayerService.removeLogicDeleted(Arrays.asList(wallPlayerIds.split(",")));
		 }
		 return Result.ok("????????????");
	 }


	 /**
	  * ????????????
	  */
	 @AutoLog(value = "wall_player-????????????")
	 @RequestMapping(value = "/changePlayerPassword", method = RequestMethod.PUT)
	 public Result<?> changePlayerPassword(@RequestBody WallPlayer player) {
		 WallPlayer p = this.wallPlayerService.getOne(new LambdaQueryWrapper<WallPlayer>().eq(WallPlayer::getPlayerName, player.getPlayerName()));
		 if (p == null) {
			 return Result.error("??????????????????");
		 }
		 player.setId(p.getId());
		 return wallPlayerService.changePassword(player);
	 }

}
