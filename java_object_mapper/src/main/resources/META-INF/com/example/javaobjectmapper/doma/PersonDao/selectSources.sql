select
    id,
    first_name,
    last_name,
    age,
    email,
    city,
    street,
    postal_code,
    loyalty_points,
    created_at
from source_people
order by id
limit /* limit */1000
offset /* offset */0
