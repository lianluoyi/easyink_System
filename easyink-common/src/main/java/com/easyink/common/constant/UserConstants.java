package com.easyink.common.constant;

/**
 * 用户常量信息
 *
 * @author admin
 */
public class UserConstants {
    /**
     * 平台内系统用户的唯一标志
     */
    public static final String SYS_USER = "SYS_USER";

    /**
     * 正常状态
     */
    public static final String NORMAL = "0";

    /**
     * 异常状态
     */
    public static final String EXCEPTION = "1";

    /**
     * 用户封禁状态
     */
    public static final String USER_DISABLE = "1";

    /**
     * 角色封禁状态
     */
    public static final String ROLE_DISABLE = "1";

    /**
     * 部门正常状态
     */
    public static final String DEPT_NORMAL = "0";

    /**
     * 部门停用状态
     */
    public static final String DEPT_DISABLE = "1";

    /**
     * 字典正常状态
     */
    public static final String DICT_NORMAL = "0";

    /**
     * 是否为系统默认（是）
     */
    public static final String YES = "Y";

    /**
     * 是否菜单外链（是）
     */
    public static final String YES_FRAME = "0";

    /**
     * 是否菜单外链（否）
     */
    public static final String NO_FRAME = "1";

    /**
     * 菜单类型（目录）
     */
    public static final String TYPE_DIR = "M";

    /**
     * 菜单类型（菜单）
     */
    public static final String TYPE_MENU = "C";

    /**
     * 菜单类型（按钮）
     */
    public static final String TYPE_BUTTON = "F";
    /**
     * 菜单类型（页面）
     */
    public static final String TYPE_PAGE = "P";

    /**
     * Layout组件标识
     */
    public static final String LAYOUT = "Layout";

    /**
     * 校验返回结果码
     */
    public static final String UNIQUE = "0";
    public static final String NOT_UNIQUE = "1";

    /**
     * 初始化的管理员角色 ID
     */
    public static final Long INIT_ADMIN_ROLE_ID = 1L;
    /**
     * 初始化的部门经理角色 ID
     */
    public static final Long INIT_DEPARTMENT_MANAGER_ROLE_ID = 6L;
    /**
     * 初始化的员工角色 ID
     */
    public static final Long INIT_EMPLOYEE_ROLE_ID = 7L;
    /**
     * 初始化的管理员角色名称
     */
    public static final String INIT_ADMIN_ROLE_NAME = "管理员";
    /**
     * 初始化的部门管理员角色名称
     */
    public static final String INIT_DEPARTMENT_ADMIN_ROLE_NAME = "部门管理员";
    /**
     * 初始化的普通员工角色名称
     */
    public static final String INIT_EMPLOYEE_ROLE_NAME = "普通员工";
    /**
     * 初始化的管理员角色key
     */
    public static final String INIT_ADMIN_ROLE_KEY = "admin";
    /**
     * 初始化的部门管理员角色key
     */
    public static final String INIT_DEPARTMENT_ADMIN_ROLE_KEY = "depart";
    /***
     * 初始化的普通员工角色key
     */
    public static final String INIT_EMPLOYEE_ROLE_KEY = "employee";
    /**
     * 最上级菜单的父菜单id
     */
    public static final String ROOT_MENU_PARENT_ID = "0";
    /**
     * 初始化的管理员默认所有菜单id
     */
    public static final String ADMIN_DEFAULT_MENU_IDS = "1,2,101,102,108,109,110,111,112,500,501,1009,1010,1011,1014,1015,1016,1018,1019,1020,1041,1044,1048,1050,1051,1052,1053,1054,2001,2002,2003,2004,2005,2006,2007,2010,2013,2014,2015,2016,2020,2021,2022,2023,2024,2025,2026,2028,2029,2051,2052,2053,2056,2060,2062,2071,2072,2073,2074,2076,2079,2080,2081,2082,2083,2084,2085,2086,2100,2101,2102,2105,2106,2107,2120,2124,2127,2131,2133,2134,2135,2138,2139,2141,2142,2151,2152,2153,2156,2157,2158,2159,2160,2163,2164,2165,2166,2179,2188,2189,2196,2201,2205,2206,2207,2208,2210,2211,2212,2213,2214,2215,2216,2217,2218,2219,2221,2222,2223,2224,2226,2227,2228,2229,2230,2231,2232,2233,2234,2235,2236,2237,2238,2239,2240,2241,2242,2243,2245,2247,2248,2249,2250,2251,2252,2256,2257,2258,2259,2260,2261,2262,2263,2264,2265,2266,2267,2268,2269,2270,2271,2272,2273,2274,2275,2276,2277,2280,2281,2282,2283,2284,2285,2286,2287,2288,2289,2292,2293,2296,2297,2298,2299,2301,2302";
    /**
     * 初始化的部门管理员默认的所有菜单id
     */
    public static final String DEPARTMENT_ADMIN_DEFAULT_IDS = "1,1018,1019,1020,2001,2002,2003,2004,2005,2006,2007,2013,2014,2015,2016,2020,2021,2022,2023,2024,2025,2026,2028,2029,2052,2053,2056,2060,2062,2074,2079,2080,2081,2082,2083,2084,2085,2086,2100,2101,2102,2105,2106,2107,2120,2124,2127,2131,2133,2134,2135,2138,2139,2141,2142,2151,2152,2153,2156,2157,2158,2160,2163,2164,2179,2188,2201,2205,2206,2207,2208,2210,2211,2212,2213,2214,2215,2217,2218,2219,2221,2222,2223,2224,2236,2238,2243,2245,2247,2248,2250,2251,2252,2256,2257,2258,2259,2260,2261,2262,2263,2264,2265,2266,2267,2268,2272,2273,2274,2275,2276,2277,2280,2282,2283,2285,2286,2287,2288,2289,2292,2293,2296,2297,2298,2299,2302";
    /**
     * 初始化的员工默认的所有菜单id
     */
    public static final String EMPLOYEE_DEFAULT_IDS = "2001,2002,2003,2006,2007,2014,2015,2016,2020,2021,2022,2028,2074,2079,2080,2105,2106,2107,2160,2163,2164,2188,2224,2236,2243,2280,2062,2282,2299";
    /**
     * 三方应用需要屏蔽的菜单
     */
    public static final String THIRD_APP_BAN_MENU_IDS = "102,109,110,111,112,2284";
    /**
     * 系统默认字段
     */
    public static final String[] SYS_DEFAULT_PROPERTIES = {"客户", "备注", "来源", "添加时间", "所属员工", "标签", "所属部门", "客户状态", "出生日期", "电话", "邮箱", "地址", "描述"};

}
