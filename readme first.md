Приложение поставляется в виде архива и содержит помимо исходного кода и всего прочего файл `docker-compose.yml`.
Для запуска контейнеров приложения требуется  **Compose 1.6.0+** и **Docker Engine 1.10.0+**.
Команды для запуска через docker-compose
```
docker-compose build

docker-compose up
```

## Эндпоинты
Примеры запросов к эндпоинтам представлены ниже. В качестве `host` необходимо указать адрес сервиса, порт 4567 

### Прием данных синхронизации от пользователя
Запрос на **сохранение** данных для клиента с uuid = 0000001
```
POST /customer/0000001/data
Host: host:4567
Content-Type: application/json
{  
   "money": 101,
   "country": "RUS"
}
```
В случае успеха в ответ придет стандартный HTTP код состояния `200 OK`  

### Отправка данных пользователю
Запрос на **получение** данных для клиента с uuid = 0000001
```
GET /customer/0000001/data
Host: host:4567
Content-Type: application/json
```
Ответ в случае успеха
Статус `200 OK`
```
{
  "money": 101,
  "country": "RUS"
}
```

### Прием игровой статистики от пользователя
Запрос на **сохранение** данных для клиента с uuid = 0000001
```
POST /customer/0000001/activity
Host: host:4567
Content-Type: application/json
{
	"value": 101
}
```
В случае успеха в ответ придет стандартный HTTP код состояния `200 OK`

## Требования от отдела аналитиков
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
SELECT country, COUNT(*) FROM carx.customer 
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
