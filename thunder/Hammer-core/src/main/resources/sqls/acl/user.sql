/*
find.user.region.with.belong.info.by.user.id
*/
SELECT
	c.*, CASE ur.id IS NULL
WHEN 1 THEN
	''
ELSE
	'selected'
END AS has_region
FROM
	t_codebook c
LEFT JOIN t_code_book_group g ON c.c_group_id = g.id
LEFT JOIN (
	SELECT
		*
	FROM
		t_user_region
	WHERE
		ur_user_id = @id
) ur ON c.c_name = ur.ur_region_code
LEFT JOIN t_user u ON ur.ur_user_id = u.id
WHERE
	g.g_name = @groupName
	
/*
list.user.by.role.and.region
*/
SELECT DISTINCT
	u.*
FROM
	t_user u
LEFT JOIN t_user_role ur ON u.id = ur.u_id
LEFT JOIN t_role r ON r.id = ur.r_id
LEFT JOIN t_user_region ure ON u.id = ure.ur_user_id
$condition
/*
list.regions.by.user.id
*/
SELECT
	c.*
FROM
	t_codebook c
LEFT JOIN t_user_region ur ON ur.ur_region_code = c.c_name
WHERE
	ur.ur_user_id = @userId