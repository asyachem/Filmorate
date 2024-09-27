# java-filmorate
Template repository for Filmorate project.

Ссылка на схему базы данных: https://dbdiagram.io/d/66f69ccf3430cb846cd530d9

Примеры основных запросов: 

```
SELECT * 
FROM films;

SELECT f.name, f.description
from films as f 
LEFT OUTER JOIN genre as g ON f.genre_id = g.id
WHERE g.name = 'Комедия';

SELECT f.name as title
FROM films as f 
GROUP BY f.name
ORDER BY COUNT(f.liked_user_id) DESC
LIMITS 10;
```