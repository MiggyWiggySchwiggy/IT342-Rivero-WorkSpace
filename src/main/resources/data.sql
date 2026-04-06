-- Seed default roles (ignore if they already exist)
INSERT IGNORE INTO roles (role_name) VALUES ('ROLE_USER');
INSERT IGNORE INTO roles (role_name) VALUES ('ROLE_ADMIN');

-- Seed sample spaces used by the frontend (ignore if already present)
INSERT IGNORE INTO spaces (
	id, name, location, type, capacity, hourly_rate, rating, available, description, image_url
) VALUES
('sample-pod-1', 'Focus Pod A1', 'Cebu IT Park', 'Pod', 1, 120.00, 4.7, true,
 'A quiet solo pod built for focused tasks and private calls.',
 'https://images.unsplash.com/photo-1497366216548-37526070297c?auto=format&fit=crop&w=1400&q=80'),
('sample-meeting-1', 'Sprint Room B', 'Ayala Center Cebu', 'Meeting Room', 8, 450.00, 4.8, true,
 'Collaborative room with display and whiteboard for team planning.',
 'https://images.unsplash.com/photo-1524758631624-e2822e304c36?auto=format&fit=crop&w=1400&q=80'),
('sample-studio-1', 'Creator Studio C3', 'Mandaue City', 'Studio', 4, 380.00, 4.6, true,
 'Flexible studio setup suitable for recording and content creation.',
 'https://images.unsplash.com/photo-1497215842964-222b430dc094?auto=format&fit=crop&w=1400&q=80'),
('sample-boardroom-1', 'Boardroom Skyline', 'Cebu Business Park', 'Boardroom', 14, 900.00, 4.9, true,
 'Premium executive boardroom with skyline views and presentation screens.',
 'https://images.unsplash.com/photo-1486406146926-c627a92ad1ab?auto=format&fit=crop&w=1400&q=80');
