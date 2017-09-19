package com.sgd.crud.test;

import java.util.UUID;

import org.apache.ibatis.session.SqlSession;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.sgd.crud.bean.Department;
import com.sgd.crud.bean.Employee;
import com.sgd.crud.dao.DepartmentMapper;
import com.sgd.crud.dao.EmployeeMapper;

/**
 * 测试dao层工作
 * spring的项目可以使用spring的单元测试，可以自动注入组件
 * 1、导入SpringTest模块
 * 2、@ContextConfiguration指定spring配置文件的位置
 * 3、直接 autowired使用要使用的组件
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations= {"classpath:applicationContext.xml"})
public class MapperTest {
	
	@Autowired
	DepartmentMapper departmentMapper;
	
	@Autowired
	EmployeeMapper employeeMapper;
	
	@Autowired
	SqlSession sqlSession;
	/**
	 * 测试DepartmentMapper
	 */
	@Test
	public void testCRUD() {
		/*//1、chuangj springioc容器
		ApplicationContext ioc = new ClassPathXmlApplicationContext("applicationContext.xml");
		//2、从容器获取mapper
		DepartmentMapper bean = ioc.getBean(DepartmentMapper.class);
		*/
		System.out.println(departmentMapper);
		
		//1、插入几个部门
	//	departmentMapper.insertSelective(new Department(null,"开发部"));
	//	departmentMapper.insertSelective(new Department(null,"测试部"));
		
		//2、生成员工数据
		//employeeMapper.insertSelective(new Employee(null, "Jerry", "M", "Jerry@sgd.com", 1));
		//3、批量插入多个员工，批量，使用可以执行批量执行的sqlSession
		/* for(){
		 	employeeMapper.insertSelective(new Employee(null, "Jerry", "M", "Jerry@sgd.com", 1));
		   }
		 */
		EmployeeMapper mapper = sqlSession.getMapper(EmployeeMapper.class);
		for(int i = 0; i < 1000; i++) {
			String uid = UUID.randomUUID().toString().substring(0,5) + i;
			mapper.insertSelective(new Employee(null,uid,"M",uid+"@sgd.com",1));
		}
		System.out.println("批量完成");
	}
}
