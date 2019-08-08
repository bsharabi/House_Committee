use heroku_e41c452f428bb7d;

select userName, hashedPassword, role from heroku_e41c452f428bb7d.users where userName = "aa";


select users.buildingNumber, users.apartmentNumber, payments.paymentSum, payments.paymentDate
from users
join tenants on tenants.userId = users.userId
join payments on tenants.idTenants = payments.idTenants
where payments.idTenants = 12 and users.buildingNumber = 22;

select *
from (
select `idCommittee` as "id", `firstName`,  `lastName`,  `userName`,  `hashedPassword`, `lastLogin`,  `registrationDate`,`seniority`, `apartmentNumber`,`buildingNumber`,  `role` , '' as "monthlyPayment"
from users
join committees on  users.userId = committees.userId
where userName = "aa" -- ?
union
select `idTenants`, `firstName`,  `lastName`,  `userName`,  `hashedPassword`, `lastLogin`,  `registrationDate`,''as "seniority", `apartmentNumber`,`buildingNumber`,  `role`, `monthlyPayment`
from users
join tenants on users.userId = tenants.userId
where userName = "aa" -- ?
) as u;


select paymentDate, paymentSum
from payments
where idTenants = 42 -- ?
order by paymentDate;

-- 1 get payments by tenants id 
select *
from(
select paymentDate, paymentSum, tenants.idTenants as "idTenants"
from payments
join tenants on tenants.idTenants = payments.idTenants
) as u
where u.idTenants = 42;

-- 2 get payments by building number
select *
from (
select paymentDate, paymentSum,users.apartmentNumber,concat(users.firstName," ", users.lastName) as "name", users.buildingNumber  -- tenants.idTenants as "idTenants"
from payments
join tenants on tenants.idTenants = payments.idTenants
join users on users.userId = tenants.userId
) as u
where u.buildingNumber = 22;

-- 3 insert new payment
INSERT payments
(`paymentSum`,
`idTenants`,
`paymentDate`)
VALUES
(44,
2,
"2019-06-18");
-- 4 get payment summary by Month
select sum(paymentSum) as "sum", month(paymentDate) as "month"
from (
select paymentDate, paymentSum, tenants.idTenants as "idTenants", users.buildingNumber
from payments
join tenants on tenants.idTenants = payments.idTenants
join users on users.userId = tenants.userId
) as u
where u.buildingNumber = 22
group by month(u.paymentDate) -- with rollup








