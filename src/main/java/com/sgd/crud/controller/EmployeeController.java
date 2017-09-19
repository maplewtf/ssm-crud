package com.sgd.crud.controller;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sgd.crud.bean.Employee;
import com.sgd.crud.bean.Msg;
import com.sgd.crud.service.EmployeeService;
/**
 * 处理CRUD请求
 *
 */
@Controller
public class EmployeeController {

	@Autowired
	EmployeeService employeeService;
	
	/**
	 * 单个或批量删除员工
	 * @param id
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/emp/{ids}", method=RequestMethod.DELETE)
	public Msg deleteEmpById(@PathVariable("ids")String ids) {
		//批量删除
		if(ids.contains("-")) {
			List<Integer> del_ids = new ArrayList<>();
			String[] str_ids = ids.split("-");
			for(String string : str_ids) {
				del_ids.add(Integer.parseInt(string));
			}
			employeeService.deleteBatch(del_ids);
		} else {
			//单个删除
			Integer id = Integer.parseInt(ids);
			employeeService.deleteEmp(id);
		}

		return Msg.success();
	}
	
	/**
	 * 如果直接发送ajax=put的请求
	 * Employee全是null
	 * 请求体中有数据，但是封装不了
	 * 原因:tomcat1、将请求题中的数据封装成一个map
	 * 			 2、request.getParameter("")会从这个map中取值
	 * 			3、SpringMVC封装pojo对象时会把pojo每个属性中的值request.getParameter("")
	 * 			但是这个也获取不了
	 * 原因:
	 * Ajax发送PUT请求时，tomcat一看是PUT请求就不会封装请求数据为map，只有post才会
	 * Spring提供了过滤器 HttpPutFormContentFilter
	 * 要支持PUT之类的请求，要封装请求中的数据，要配置上HttpPutFormContentFilter，
	 * 作用：将请求体中的数据解析包装成map，重写getgetParameter
	 * 保存更新
	 * @param employee
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/emp/{empId}", method=RequestMethod.PUT)
	public Msg saveEmp(Employee employee) {
		//System.out.println(employee.toString());
		employeeService.updateEmp(employee);
		return Msg.success();
	}

	
	/**
	 * 查询id查询员工
	 */
	@RequestMapping(value="/emp/{id}", method=RequestMethod.GET)
	@ResponseBody
	public Msg getEmp(@PathVariable("id")Integer id) {
		System.out.println(id);
		Employee employee = employeeService.getEmp(id);
		return Msg.success().add("emp", employee);
	}
	
	/*检查用户名是否可用*/
	@ResponseBody
	@RequestMapping("/checkuser")
	public Msg checkuser(@RequestParam("empName")String empName) {
		//先判断用户名是否是合法的表达式
		String regx = "(^[a-zA-Z0-9_-]{6,16}$)|(^[\u2E80-\u9FFF]{2,5})";
		if(!empName.matches(regx)) {
			return Msg.fail().add("va_msg", "用户名必须是 6-16 位数字和字母的组合或者2-5位中文");
		}
		//数据库用户名重复校验
		boolean b = employeeService.checkUser(empName);
		if(b) {
			return Msg.success();
		} else {
			return Msg.fail().add("va_msg", "用户名不可用");
		}
	}

	/**
	 * 员工保存
	 * 1、支持JSR303校验
	 * 导入Hibernate-Validator的包
	 */
	@RequestMapping(value="/emp", method=RequestMethod.POST)
	@ResponseBody
	public Msg saveEmp(@Valid Employee employee,BindingResult result) {
		if(result.hasErrors()) {
			//校验失败，返回失败,在模态框中显示校验失败的错误信息
			Map<String, Object> map = new HashMap<>();
			List<FieldError> errors = result.getFieldErrors();
			for(FieldError fieldError : errors) {
				//System.out.println(" 错误的字段名： " + fieldError.getField());
				//System.out.println(" 错误信息： " + fieldError.getDefaultMessage());
				map.put(fieldError.getField(), fieldError.getDefaultMessage());
			}
			return Msg.fail().add("errorFields", map);
		}else {
			employeeService.saveEmp(employee);
			return Msg.success();
		}
	}
	
	/**
	 * 导入jackson包
	 * @param pn
	 * @param model
	 * @return
	 */
	@RequestMapping("/emps")
	@ResponseBody
	public Msg  getEmpsWithJson(
			@RequestParam(value="pn", defaultValue="1")Integer pn,Model model) {
		//引入PageHelper分页插件
		//在查询之前只需要调用。传入页码，以及每页大小
		PageHelper.startPage(pn,5);
		//startPage后面紧跟的查询就是分页查询
		List<Employee> emps = employeeService.getAll();
		//pageInfo包装查询后的结果，只需要将pageINfo交给页面
		//封装了详细的分页信息，包括我们查询出来的数据，传入连续显示的页数
		PageInfo page = new PageInfo(emps,5);
		
		return Msg.success().add("pageInfo",page);
	}
	
	/**
	 * 查询员工数据(分页查询)
	 * @return
	 */
	//@RequestMapping("/emps")
	public String getEmps(@RequestParam(value="pn", defaultValue="1")Integer pn,Model model) {
		//引入PageHelper分页插件
		//在查询之前只需要调用。传入页码，以及每页大小
		PageHelper.startPage(pn,5);
		//startPage后面紧跟的查询就是分页查询
		List<Employee> emps = employeeService.getAll();
		//pageInfo包装查询后的结果，只需要将pageINfo交给页面
		//封装了详细的分页信息，包括我们查询出来的数据，传入连续显示的页数
		PageInfo page = new PageInfo(emps,5);
		model.addAttribute("pageInfo", page);
		return "list";
	}
}
