insert into region(name, x_pos, y_pos) values ('테스트', 111, 111);
insert into short_forecast(base_date, base_time, fcst_date, fcst_time, nx, ny,
                           created_at, modified_at, short_forecast_id, category, fcst_value)
values ('3000-01-01', '20:00:00', '3000-01-01', '21:00:00', 62, 123,
        '2024-08-13 20:12:19.121811', '2024-08-13 20:12:19.121811', 1, 'POP', '60'),
       ('3000-01-01', '20:00:00', '3000-01-01', '22:00:00', 62, 123,
        '2024-08-13 20:12:19.121811', '2024-08-13 20:12:19.121811', 2, 'POP', '80')
