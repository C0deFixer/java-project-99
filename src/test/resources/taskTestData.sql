INSERT INTO users (id, email, first_name)
values (33, 'testUser@hexlet.com', 'Kirill');
INSERT INTO users (id, email, first_name)
values (34, 'vasyaUser@yandex.com', 'Vaysa');
INSERT INTO users (id, email, first_name)
values (35, 'patyaUser@gmail.com', 'Petya');
INSERT INTO lables (id, name)
values (11, 'testLabel');
INSERT INTO lables (id, name)
values (12, 'testLabelFilter');
INSERT INTO lables (id, name)
values (13, 'myLabel');
INSERT INTO task_statuses (id, name, slug)
values (10, 'bugs', 'bugs');
INSERT INTO task_statuses (id, name, slug)
values (20, 'try', 'try to repeat');
INSERT INTO task_statuses (id, name, slug)
values (30, 'dump', 'dump');
INSERT INTO tasks (id, index, name, description, user_id, task_status_id) values (10, 15, 'my first', 'do nothing',33, 10);
INSERT INTO tasks (id, index, name, description, user_id, task_status_id) values (20, 16, 'super urgent task', 'do nothing', 34 , 20);
INSERT INTO tasks (id, index, name, description, user_id, task_status_id) values (30, 17, 'not urgent', 'doing  anything',34 , 20);
INSERT INTO tasks (id, index, name, description, user_id, task_status_id) values (40, 18, 'one more not so urgent', 'bla bla bla',33, 10);
INSERT INTO tasks (id, index, name, description, user_id, task_status_id) values (50, 19, 'keep hands out', 'please be careful',33, 20);
INSERT INTO tasks (id, index, name, description, user_id, task_status_id) values (60, 20, 'thing before', 'only fore experts',33, 30);
INSERT INTO tasks (id, index, name, description, user_id, task_status_id) values (70, 21, 'tdd', 'test drive development',33, 10);
INSERT INTO tasks (id, index, name, description, user_id, task_status_id) values (80, 22, 'before fix', 'some bugs need to be fixed',33, 20);
INSERT INTO task_labels (task_id, label_id) values (10, 11);
INSERT INTO task_labels (task_id, label_id) values (40, 11);
INSERT INTO task_labels (task_id, label_id) values (70, 11);
INSERT INTO task_labels (task_id, label_id) values (10, 12);
INSERT INTO task_labels (task_id, label_id) values (20, 12);
INSERT INTO task_labels (task_id, label_id) values (30, 12);
INSERT INTO task_labels (task_id, label_id) values (40, 12);
INSERT INTO task_labels (task_id, label_id) values (50, 12);
INSERT INTO task_labels (task_id, label_id) values (60, 12);
INSERT INTO task_labels (task_id, label_id) values (70, 12);
INSERT INTO task_labels (task_id, label_id) values (80, 12);
INSERT INTO task_labels (task_id, label_id) values (40, 13);
INSERT INTO task_labels (task_id, label_id) values (60, 13);
INSERT INTO task_labels (task_id, label_id) values (80, 13);