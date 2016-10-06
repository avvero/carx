### Выбирать Х пользователей с самым большим текущим значением "money" по каждой стране "country".
 
Коли-во пользователей в выборке по стране указывается в $slice.
Ниже приведен пример запроса к mongodb для получения 2 пользователей по каждой стране.

```mongo
db.getCollection('customerData').aggregate([
    { "$sort": { "money": -1 } },
    {$group: {_id:'$country', list: {$push: {_id: '$_id', money: '$money'}}}},
    {$project: {_id:'$_id', nRichestMen: {$slice: ['$list', 0, 2]}}}
]);
```

### Подсчитать количество новых пользователей по каждой стране за период Х

Ниже приведен пример запроса к mysql DB для получения количества пользователей, 
зарегистрированных в период с 2016-10-01 по 2016-10-07.

```sql
SELECT COUNT(*) FROM carx.customer 
WHERE created >= '2016-10-01' AND created < '2016-10-07'
GROUP BY country;
```

### Для каждого конкретного пользователя X быстро получить отсортированный по дате список значений показателя "activity" и дату его получения за период Y.

Ниже приведен пример запроса к mysql DB для получения значений показателя "activity" для для пользователя с uuid = 1

```sql
SELECT activity.value, activity.created FROM activity, customer
WHERE 
activity.customer_id = customer.id
AND customer.uuid = '1'
ORDER BY created DESC;
```