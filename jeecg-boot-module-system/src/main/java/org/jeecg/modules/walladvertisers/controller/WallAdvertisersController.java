package org.jeecg.modules.walladvertisers.controller;

import java.util.*;
import java.util.stream.Collectors;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.apache.commons.lang.StringUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.util.JwtUtil;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.advertisersoffer.entity.WallAdvertisersOffer;
import org.jeecg.modules.advertisersoffer.service.IWallAdvertisersOfferService;
import org.jeecg.modules.base.service.BaseCommonService;
import org.jeecg.modules.system.entity.SysRole;
import org.jeecg.modules.system.entity.SysTenant;
import org.jeecg.modules.system.entity.SysUserRole;
import org.jeecg.modules.system.service.ISysUserRoleService;
import org.jeecg.modules.walladvertisers.entity.WallAdvertisers;
import org.jeecg.modules.walladvertisers.service.IWallAdvertisersService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.jeecg.common.system.base.controller.JeecgController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.jeecg.common.aspect.annotation.AutoLog;

 /**
 * @Description: wall_advertisers
 * @Author: jeecg-boot
 * @Date:   2021-05-25
 * @Version: V1.0
 */
@Api(tags="wall_advertisers")
@RestController
@RequestMapping("/walladvertisers/wallAdvertisers")
@Slf4j
public class WallAdvertisersController extends JeecgController<WallAdvertisers, IWallAdvertisersService> {
	@Autowired
	private IWallAdvertisersService wallAdvertisersService;

	 @Resource
	 private BaseCommonService baseCommonService;

	 @Autowired
	 private IWallAdvertisersOfferService advertisersOfferService;
	/**
	 * ??????????????????
	 *
	 * @param wallAdvertisers
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "wall_advertisers-??????????????????")
	@ApiOperation(value="wall_advertisers-??????????????????", notes="wall_advertisers-??????????????????")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(WallAdvertisers wallAdvertisers,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		wallAdvertisers.setDelFlag(0);
		QueryWrapper<WallAdvertisers> queryWrapper = QueryGenerator.initQueryWrapper(wallAdvertisers, req.getParameterMap());
		Page<WallAdvertisers> page = new Page<WallAdvertisers>(pageNo, pageSize);
		IPage<WallAdvertisers> pageList = wallAdvertisersService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	 @AutoLog(value = "wall_advertisers-?????????????????????")
	 @RequestMapping(value = "/queryallAdvertiser", method = RequestMethod.GET)
	 public Result<List<WallAdvertisers>> queryallAdvertiser(@RequestParam(name="ids",required=false) String ids) {
		 Result<List<WallAdvertisers>> result = new Result<List<WallAdvertisers>>();
		 LambdaQueryWrapper<WallAdvertisers> query = new LambdaQueryWrapper<>();
		 query.eq(WallAdvertisers::getDelFlag, 0);
		 if(oConvertUtils.isNotEmpty(ids)){
			 query.in(WallAdvertisers::getId, ids.split(","));
		 }
		 //??????????????????????????????
		 List<WallAdvertisers> ls = wallAdvertisersService.list(query);
		 result.setSuccess(true);
		 result.setResult(ls);
		 return result;
	 }
	 @AutoLog(value = "wall_advertisers-??????offer????????????")
	 @RequestMapping(value = "/queryOfferAdvertiser", method = RequestMethod.GET)
	 public Result<List<String>> queryOfferAdvertiser(@RequestParam(name = "advertisersId", required = true) String advertisersId) {
		 Result<List<String>> result = new Result<>();
		 List<String> list = new ArrayList<String>();
		 WallAdvertisers advertiser = wallAdvertisersService.getById(advertisersId);
		 if (advertiser == null ) {
			 result.error500("??????????????????????????????");
		 } else {
		 	list.add(advertiser.getId());
			 result.setSuccess(true);
			 result.setResult(list);
		 }
		 return result;
	 }
	 /**
	 *   ??????
	 *
	 * @param wallAdvertisers
	 * @return
	 */
	@AutoLog(value = "wall_advertisers-??????")
	@ApiOperation(value="wall_advertisers-??????", notes="wall_advertisers-??????")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody WallAdvertisers wallAdvertisers) {
		wallAdvertisers.setDelFlag(0);
		wallAdvertisersService.save(wallAdvertisers);
		return Result.OK("???????????????");
	}

	/**
	 *  ??????
	 *
	 * @param wallAdvertisers
	 * @return
	 */
	@AutoLog(value = "wall_advertisers-??????")
	@ApiOperation(value="wall_advertisers-??????", notes="wall_advertisers-??????")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody WallAdvertisers wallAdvertisers) {
		wallAdvertisersService.updateById(wallAdvertisers);
		return Result.OK("????????????!");
	}

	/**
	 *   ??????id??????
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "wall_advertisers-??????id??????")
	@ApiOperation(value="wall_advertisers-??????id??????", notes="wall_advertisers-??????id??????")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
//		wallAdvertisersService.removeById(id);
//		return Result.OK("????????????!");
		Result<WallAdvertisers> result = new Result<WallAdvertisers>();
		try {
			WallAdvertisers advertisers = wallAdvertisersService.getById(id);
			if(advertisers==null) {
				result.error500("?????????????????????");
			}else{
				advertisers.setDelFlag(1);
				wallAdvertisersService.updateById(advertisers);
				result.success("????????????!");
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			result.error500("????????????");
		}
		return result;
	}
	 /**
	  * ??????&??????advertisers
	  * @param jsonObject
	  * @return
	  */
	 //@RequiresRoles({"admin"})
	 @AutoLog(value = "wall_advertisers-??????&??????advertisers")
	 @RequestMapping(value = "/frozenBatch", method = RequestMethod.PUT)
	 public Result<WallAdvertisers> frozenBatch(@RequestBody JSONObject jsonObject) {
		 Result<WallAdvertisers> result = new Result<WallAdvertisers>();
		 try {
			 String ids = jsonObject.getString("ids");
			 String status = jsonObject.getString("status");
			 String[] arr = ids.split(",");
			 for (String id : arr) {
				 if(oConvertUtils.isNotEmpty(id)) {
					 this.wallAdvertisersService.update(new WallAdvertisers().setStatus(Integer.parseInt(status)),
							 new UpdateWrapper<WallAdvertisers>().lambda().eq(WallAdvertisers::getId,id));
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
	 *  ????????????
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "wall_advertisers-????????????")
	@ApiOperation(value="wall_advertisers-????????????", notes="wall_advertisers-????????????")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
//		this.wallAdvertisersService.removeByIds(Arrays.asList(ids.split(",")));
//		return Result.OK("??????????????????!");
		Result<WallAdvertisers> result = new Result<WallAdvertisers>();
		try {
			String[] arr = ids.split(",");
			for (String id : arr) {
				if(oConvertUtils.isNotEmpty(id)) {
					this.wallAdvertisersService.update(new WallAdvertisers().setDelFlag(Integer.parseInt("1")),
							new UpdateWrapper<WallAdvertisers>().lambda().eq(WallAdvertisers::getId,id));
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
	 * ??????id??????
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "wall_advertisers-??????id??????")
	@ApiOperation(value="wall_advertisers-??????id??????", notes="wall_advertisers-??????id??????")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		WallAdvertisers wallAdvertisers = wallAdvertisersService.getById(id);
		if(wallAdvertisers==null) {
			return Result.error("?????????????????????");
		}
		return Result.OK(wallAdvertisers);
	}

    /**
    * ??????excel
    *
    * @param request
    * @param wallAdvertisers
    */
    @AutoLog(value = "wallAdvertisers--??????excel")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, WallAdvertisers wallAdvertisers) {
        return super.exportXls(request, wallAdvertisers, WallAdvertisers.class, "wall_advertisers");
    }

    /**
      * ??????excel????????????
    *
    * @param request
    * @param response
    * @return
    */
	@AutoLog(value = "wallAdvertisers--????????????")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, WallAdvertisers.class);
    }
	 /**
	  * ????????????????????????advertiser??????????????????
	  *
	  * @return logicDeletedAdvertiserList
	  */
	 @AutoLog(value = "wallAdvertisers--????????????????????????advertiser??????")
	 @GetMapping("/recycleBin")
	 public Result getRecycleBin() {
		 List<WallAdvertisers> logicDeletedAdvertiserList = wallAdvertisersService.queryLogicDeleted();
		 return Result.ok(logicDeletedAdvertiserList);
	 }

	 /**
	  * ????????????????????????advertiser
	  *
	  * @param jsonObject
	  * @return
	  */
	 @AutoLog(value = "wallAdvertisers--????????????????????????advertiser")
	 @RequestMapping(value = "/putRecycleBin", method = RequestMethod.PUT)
	 public Result putRecycleBin(@RequestBody JSONObject jsonObject, HttpServletRequest request) {
		 String advertiserIds = jsonObject.getString("advertiserIds");
		 if (StringUtils.isNotBlank(advertiserIds)) {
			 WallAdvertisers updateAdvertiser = new WallAdvertisers();
			 updateAdvertiser.setUpdateBy(JwtUtil.getUserNameByToken(request));
			 updateAdvertiser.setUpdateTime(new Date());
			 wallAdvertisersService.revertLogicDeleted(Arrays.asList(advertiserIds.split(",")), updateAdvertiser);
		 }
		 return Result.ok("????????????");
	 }

	 /**
	  * ????????????advertiser
	  *
	  * @param advertiserIds ????????????advertiserID?????????id?????????????????????
	  * @return
	  */
	 //@RequiresRoles({"admin"})
	 @AutoLog(value = "wallAdvertisers--????????????advertiser")
	 @RequestMapping(value = "/deleteRecycleBin", method = RequestMethod.DELETE)
	 public Result deleteRecycleBin(@RequestParam("advertiserIds") String advertiserIds) {
		 if (StringUtils.isNotBlank(advertiserIds)) {
			 wallAdvertisersService.removeLogicDeleted(Arrays.asList(advertiserIds.split(",")));
		 }
		 return Result.ok("????????????");
	 }
}
