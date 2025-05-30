-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: May 30, 2025 at 06:16 PM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `quiz_game`
--

-- --------------------------------------------------------

--
-- Table structure for table `accounts`
--

CREATE TABLE `accounts` (
  `id` int(11) NOT NULL,
  `email` varchar(100) NOT NULL,
  `name` varchar(100) NOT NULL,
  `password` varchar(255) NOT NULL,
  `coins` int(11) NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `accounts`
--

INSERT INTO `accounts` (`id`, `email`, `name`, `password`, `coins`) VALUES
(1, 'test@example.com', 'Test User', '$2y$10$PIX28PsoSPFdQBINFUryJetRenP7ZAfwrCOs3VX.JH0BdYKb409gu', 0),
(2, 'boss1@gmail.com', 'boss1', '$2y$10$1Wg18Nzow8soToTogqZUZuOIm6e/44HVBgZ0D1lNixdgGQPolqXbm', 904297),
(5, 'boss2@gmail.com', 'boss2', '$2y$10$McXcRiZ25aUuG/XmLk4OKOVc/U0ImForuYvD6VcQTrocWWkPZl.Xi', 3332333),
(6, 'boss5@gmail.com', 'boss5', '$2y$10$3GLLMpnvrc793FX8PVa/kOGIGQCYrRk7yaHzXeIEnZfHngQ/idjBq', 1100);

-- --------------------------------------------------------

--
-- Table structure for table `chests`
--

CREATE TABLE `chests` (
  `id` int(11) NOT NULL,
  `name` varchar(100) NOT NULL,
  `price` int(11) NOT NULL,
  `image_path` varchar(255) DEFAULT NULL COMMENT 'Path to chest image',
  `common_prob` decimal(5,2) DEFAULT 60.00,
  `rare_prob` decimal(5,2) DEFAULT 30.00,
  `epic_prob` decimal(5,2) DEFAULT 9.00,
  `legendary_prob` decimal(5,2) DEFAULT 1.00
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `chests`
--

INSERT INTO `chests` (`id`, `name`, `price`, `image_path`, `common_prob`, `rare_prob`, `epic_prob`, `legendary_prob`) VALUES
(1, 'Mystery Chest', 1000, '\"C:\\Users\\mihai\\Downloads\\unnamed.png\"', 60.00, 30.00, 9.00, 1.00);

-- --------------------------------------------------------

--
-- Table structure for table `profile_items`
--

CREATE TABLE `profile_items` (
  `id` int(11) NOT NULL,
  `name` varchar(100) NOT NULL,
  `color_code` varchar(500) NOT NULL COMMENT 'HEX color or predefined name',
  `rarity` enum('common','rare','epic','legendary') NOT NULL,
  `image_path` varchar(255) DEFAULT NULL COMMENT 'Optional path to custom profile image/background',
  `unlock_message` varchar(255) DEFAULT NULL COMMENT 'Optional special message when unlocked'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `profile_items`
--

INSERT INTO `profile_items` (`id`, `name`, `color_code`, `rarity`, `image_path`, `unlock_message`) VALUES
(12, 'Red', '#FF0000', 'common', NULL, NULL),
(13, 'Blue', '#0000FF', 'common', NULL, NULL),
(14, 'Green', '#00FF00', 'common', NULL, NULL),
(15, 'Yellow', '#FFFF00', 'common', NULL, NULL),
(16, 'Purple', '#800080', 'rare', NULL, NULL),
(17, 'Gold', '#FFD700', 'rare', NULL, NULL),
(18, 'Silver', '#C0C0C0', 'rare', NULL, NULL),
(19, 'Rainbow', 'linear-gradient(to right, red, orange, yellow, green, blue, indigo, violet)', 'epic', NULL, NULL),
(20, 'Galaxy', 'radial-gradient(circle, #000000, #1a0033, #330066)', 'epic', NULL, NULL),
(21, 'Dragon Scale', 'repeating-linear-gradient(45deg, #4a0000, #4a0000 10px, #6a0000 10px, #6a0000 20px)', 'legendary', NULL, NULL),
(22, 'Phantom', 'repeating-radial-gradient(circle, transparent, transparent 10px, #00ffff 10px, #00ffff 20px)', 'legendary', NULL, NULL);

-- --------------------------------------------------------

--
-- Table structure for table `questions`
--

CREATE TABLE `questions` (
  `id` int(11) NOT NULL,
  `question_text` text NOT NULL,
  `option1` varchar(255) NOT NULL,
  `option2` varchar(255) NOT NULL,
  `option3` varchar(255) NOT NULL,
  `option4` varchar(255) NOT NULL,
  `correct_option` int(11) NOT NULL CHECK (`correct_option` between 1 and 4),
  `explanation` text DEFAULT NULL,
  `category` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `questions`
--

INSERT INTO `questions` (`id`, `question_text`, `option1`, `option2`, `option3`, `option4`, `correct_option`, `explanation`, `category`) VALUES
(1, 'What is the capital of France?', 'Berlin', 'Madrid', 'Paris', 'Rome', 3, 'Capital of France is Paris.', 'Geography'),
(2, 'Which planet is known as the Red Planet?', 'Venus', 'Mars', 'Jupiter', 'Saturn', 2, 'Mars is the only known planet as the Red Planet.', 'Science'),
(3, '2 + 2 = ?', '3', '4', '5', '6', 2, '2 plus 2 equals 4.', 'Math'),
(4, 'What vitamin do we get from sunlight?', 'Vitamin A', 'Vitamin B', 'Vitamin C', 'Vitamin D', 4, 'Vitamin D is synthesized in the skin in response to sunlight.', 'Science'),
(5, 'How many sides does a hexagon have?', '5', '6', '7', '8', 2, 'Hexagon has 6 sides.', 'Math'),
(6, 'Which line divides the Earth into the Northern and Southern Hemispheres?', 'Prime Meridian', 'Equator', 'Tropic of Cancer', 'International Date Line', 2, 'The Equator divides Earth into the Northern and Southern Hemispheres.', 'Geography'),
(7, 'What gas do plants absorb from the atmosphere?', 'Oxygen', 'Nitrogen', 'Carbon Dioxide', 'Hydrogen', 3, 'Plants absorb carbon dioxide for photosynthesis.', 'Science'),
(8, 'What is 50% of 80?', '30', '40', '50', '60', 2, '50% of 80 is 40.', 'Math'),
(9, 'Which gas is essential for humans to breathe?', 'Carbon Dioxide', 'Oxygen', 'Nitrogen', 'Hydrogen', 2, 'Humans need oxygen for respiration.', 'Science'),
(10, 'What was the name of Hitler’s political party?', 'Nazi', 'Fascist', 'Communist', 'Republican', 3, 'Hitler led the Nazi Party.', 'History'),
(11, 'Which event started WWI?', 'Pearl Harbor', 'Assassination of Franz Ferdinand', 'Fall of Berlin', 'D-Day', 2, 'The assassination of Archduke Franz Ferdinand started WWI.', 'History'),
(12, 'What is the powerhouse of the cell?', 'Nucleus', 'Mitochondria', 'Ribosome', 'Chloroplast', 2, 'The mitochondria produce energy for the cell.', 'Science'),
(13, 'What is the value of π (pi) up to 2 decimals?', '3.12', '3.14', '3.16', '3.18', 2, 'Pi is approximately 3.14.', 'Math'),
(14, 'If a triangle has angles 90° and 45°, what is the third?', '35°', '45°', '60°', '90°', 2, 'Sum of angles in triangle is 180°, so 180 - 90 - 45 = 45°.', 'Math'),
(15, 'Which is the largest continent by land area?', 'Africa', 'Asia', 'North America', 'Europe', 2, 'Asia is the largest continent by land area.', 'Geography'),
(16, 'What is the capital of Australia?', 'Sydney', 'Melbourne', 'Brisbane', 'Canberra', 4, 'Canberra is the capital of Australia.', 'Geography'),
(17, 'What wall fell in 1989?', 'Berlin Wall', 'Great Wall', 'Iron Curtain', 'Wall of China', 1, 'The Berlin Wall fell in 1989, marking the end of the Cold War.', 'History'),
(18, 'Solve: 5 + 3 × 2', '11', '16', '13', '10', 1, 'Multiplication before addition: 3 × 2 = 6, 5 + 6 = 11.', 'Math'),
(19, 'Which country is famous for its fjords?', 'Sweden', 'Finland', 'Norway', 'Denmark', 3, 'Norway is known for its stunning fjords.', 'Geography'),
(20, 'Which of the following is a mammal?', 'Shark', 'Frog', 'Whale', 'Snake', 3, 'Whales are mammals despite living in the sea.', 'Science'),
(21, 'What is the main source of energy for Earth?', 'Wind', 'Sun', 'Coal', 'Water', 2, 'The sun is the primary source of energy for Earth.', 'Science'),
(22, 'How many bones are in the adult human body?', '206', '201', '208', '212', 1, 'An adult human has 206 bones.', 'Science'),
(23, 'What is the chemical symbol for water?', 'H2O', 'O2', 'CO2', 'NaCl', 1, 'Water is chemically represented as H2O.', 'Science'),
(24, 'What is the next prime number after 7?', '8', '9', '10', '11', 4, 'Next prime after 7 is 11.', 'Math'),
(25, 'Who discovered America?', 'Christopher Columbus', 'Marco Polo', 'Leif Erikson', 'Amerigo Vespucci', 1, 'Christopher Columbus reached the Americas in 1492.', 'History'),
(26, 'Which U.S. state is the Grand Canyon located in?', 'Nevada', 'Arizona', 'Utah', 'Colorado', 2, 'The Grand Canyon is located in Arizona.', 'Geography'),
(27, 'What is the longest river in the world?', 'Amazon', 'Nile', 'Yangtze', 'Mississippi', 2, 'The Nile River is traditionally considered the longest river in the world.', 'Geography'),
(28, 'When did the Cold War end?', '1989', '1991', '1995', '1985', 2, 'The Cold War ended in 1991 with the collapse of the Soviet Union.', 'History'),
(29, 'Who invented the printing press?', 'Thomas Edison', 'Gutenberg', 'Galileo', 'Newton', 2, 'Johannes Gutenberg invented the printing press.', 'History'),
(30, 'Where was Napoleon born?', 'Corsica', 'Paris', 'Rome', 'Marseille', 1, 'Napoleon was born on the island of Corsica.', 'History'),
(31, 'What is the name of the sea between Saudi Arabia and Africa?', 'Arabian Sea', 'Red Sea', 'Black Sea', 'Mediterranean Sea', 2, 'The Red Sea lies between Saudi Arabia and Africa.', 'Geography'),
(32, 'What is the result of 100 ÷ 4?', '25', '20', '15', '30', 1, '100 divided by 4 equals 25.', 'Math'),
(33, 'What is the center of an atom called?', 'Proton', 'Electron', 'Nucleus', 'Neutron', 3, 'The nucleus is the central part of an atom.', 'Science'),
(34, 'Who was known as the Maid of Orléans?', 'Marie Antoinette', 'Joan of Arc', 'Catherine de Medici', 'Eleanor of Aquitaine', 2, 'Joan of Arc was called the Maid of Orléans.', 'History'),
(35, 'Which dynasty built the Great Wall of China?', 'Ming', 'Qing', 'Han', 'Tang', 1, 'The Ming Dynasty rebuilt and reinforced much of the Wall.', 'History'),
(36, 'What is the capital of Brazil?', 'Rio de Janeiro', 'SÃ£o Paulo', 'BrasÃxadlia', 'Salvador', 3, 'BrasÃxadlia is the capital of Brazil.', 'Geography'),
(37, 'What do bees collect from flowers?', 'Water', 'Leaves', 'Nectar', 'Seeds', 3, 'Bees collect nectar from flowers to make honey.', 'Science'),
(38, 'What is the capital of Japan?', 'Kyoto', 'Osaka', 'Tokyo', 'Hiroshima', 3, 'Tokyo is the capital of Japan.', 'Geography'),
(39, 'Which blood cells fight infections?', 'Red', 'White', 'Platelets', 'Plasma', 2, 'White blood cells help the body fight infections.', 'Science'),
(40, 'What country is the Eiffel Tower in?', 'Italy', 'Germany', 'France', 'Spain', 3, 'The Eiffel Tower is in Paris, France.', 'Geography'),
(41, 'Which river flows through Paris?', 'Thames', 'Danube', 'Seine', 'Rhine', 3, 'The Seine River flows through Paris.', 'Geography'),
(42, 'Solve: 18 ÷ (3 × 2)', '2', '3', '6', '12', 2, '3 × 2 = 6, 18 ÷ 6 = 3.', 'Math'),
(43, 'What ocean is on the east coast of the United States?', 'Indian', 'Arctic', 'Atlantic', 'Pacific', 3, 'The Atlantic Ocean borders the east coast of the U.S.', 'Geography'),
(44, 'Which country is both in Europe and Asia?', 'Turkey', 'Russia', 'Kazakhstan', 'All of the above', 4, 'All listed countries span both Europe and Asia.', 'Geography'),
(45, 'Which ship sank in 1912?', 'Titanic', 'Lusitania', 'Britannic', 'Bismarck', 1, 'The RMS Titanic sank in 1912.', 'History'),
(46, 'What is 9 × 9?', '81', '72', '99', '108', 1, '9 times 9 equals 81.', 'Math'),
(47, 'What is the smallest country in the world?', 'Monaco', 'San Marino', 'Vatican City', 'Liechtenstein', 3, 'Vatican City is the smallest country by area and population.', 'Geography'),
(48, 'What is the smallest unit of life?', 'Tissue', 'Organ', 'Cell', 'Organism', 3, 'The cell is the smallest unit of life.', 'Science'),
(49, 'Which organ pumps blood through the body?', 'Liver', 'Lungs', 'Brain', 'Heart', 4, 'The heart is responsible for pumping blood.', 'Science'),
(50, 'Which organ helps filter blood in the body?', 'Heart', 'Kidneys', 'Lungs', 'Liver', 2, 'The kidneys filter waste from the blood.', 'Science'),
(51, 'Which mountain is the highest in the world?', 'K2', 'Mount Everest', 'Kangchenjunga', 'Makalu', 2, 'Mount Everest is the tallest mountain above sea level.', 'Geography'),
(52, 'How many degrees in a right angle?', '90', '180', '45', '60', 1, 'A right angle is always 90 degrees.', 'Math'),
(53, 'What is the capital of Canada?', 'Toronto', 'Ottawa', 'Vancouver', 'Montreal', 2, 'Ottawa is the capital of Canada.', 'Geography'),
(54, 'Who was assassinated in 1963 in Dallas?', 'Martin Luther King Jr.', 'John F. Kennedy', 'Robert F. Kennedy', 'Abraham Lincoln', 2, 'JFK was assassinated in Dallas, Texas, in 1963.', 'History'),
(55, 'Who was the first Emperor of Rome?', 'Julius Caesar', 'Augustus', 'Nero', 'Caligula', 2, 'Augustus was the first emperor of Rome.', 'History'),
(56, 'What is the most abundant gas in Earth\'s atmosphere?', 'Oxygen', 'Carbon Dioxide', 'Nitrogen', 'Hydrogen', 3, 'Nitrogen makes up about 78% of Earth\'s atmosphere.', 'Science'),
(57, 'Which is the smallest prime number?', '1', '2', '3', '5', 2, '2 is the smallest and only even prime number.', 'Math'),
(58, 'What is 2^5?', '32', '16', '64', '48', 1, '2 raised to 5 is 2×2×2×2×2 = 32.', 'Math'),
(59, 'What is the square root of 144?', '10', '12', '14', '16', 2, '12 × 12 = 144.', 'Math'),
(60, 'What is the perimeter of a square with side length 4?', '12', '16', '20', '8', 2, 'Perimeter = 4 × side = 16.', 'Math'),
(61, 'What is Newton famous for?', 'Electricity', 'Laws of Motion', 'Evolution', 'Periodic Table', 2, 'Isaac Newton is known for his Laws of Motion and Gravity.', 'Science'),
(62, 'Which U.S. state is known as the \'Sunshine State\'?', 'California', 'Texas', 'Florida', 'Arizona', 3, 'Florida is nicknamed the \'Sunshine State\'.', 'Geography'),
(63, 'Who was Cleopatra?', 'Roman Queen', 'Egyptian Pharaoh', 'Greek Slave', 'Persian Princess', 2, 'Cleopatra was the last active Pharaoh of Egypt.', 'History'),
(64, 'What is 15% of 200?', '25', '30', '35', '40', 2, '15% of 200 is (15/100) × 200 = 30.', 'Math'),
(65, 'In what year did World War II end?', '1942', '1945', '1939', '1950', 2, 'World War II ended in 1945.', 'History'),
(66, 'Which empire built the Colosseum?', 'Greek', 'Roman', 'Ottoman', 'Byzantine', 2, 'The Colosseum was built by the Roman Empire.', 'History'),
(67, 'Which U.S. president abolished slavery?', 'George Washington', 'Abraham Lincoln', 'Thomas Jefferson', 'John Adams', 2, 'Abraham Lincoln issued the Emancipation Proclamation.', 'History'),
(68, 'In which city was the Titanic built?', 'Liverpool', 'Belfast', 'Glasgow', 'Dublin', 2, 'The Titanic was built in Belfast, Northern Ireland.', 'History'),
(69, 'What is the boiling point of water?', '100°C', '90°C', '80°C', '120°C', 1, 'Water boils at 100°C at sea level.', 'Science'),
(70, 'Which country has the largest population?', 'India', 'USA', 'China', 'Indonesia', 3, 'China has the largest population, though India is close.', 'Geography'),
(71, 'Which African country has the most pyramids?', 'Egypt', 'Sudan', 'Ethiopia', 'Libya', 2, 'Sudan actually has more pyramids than Egypt.', 'Geography'),
(72, 'Which planet is closest to the sun?', 'Mercury', 'Venus', 'Earth', 'Mars', 1, 'Mercury is the closest planet to the sun.', 'Science'),
(73, 'What is 1/2 + 1/4?', '1/4', '3/4', '2/4', '1/2', 2, '1/2 + 1/4 = 2/4 + 1/4 = 3/4.', 'Math'),
(74, 'What is 8 squared?', '64', '56', '48', '72', 1, '8 × 8 = 64.', 'Math'),
(75, 'What year did the French Revolution begin?', '1776', '1789', '1804', '1812', 2, 'The French Revolution began in 1789.', 'History'),
(76, 'Which part of the plant conducts photosynthesis?', 'Roots', 'Stem', 'Leaves', 'Flowers', 3, 'Photosynthesis mainly occurs in the leaves.', 'Science'),
(77, 'What is 3³?', '6', '9', '27', '18', 3, '3 cubed is 3 × 3 × 3 = 27.', 'Math'),
(78, 'What comes next: 2, 4, 8, 16, ...?', '24', '30', '32', '36', 3, 'Each number is multiplied by 2, so next is 32.', 'Math'),
(79, 'What planet is known as the Red Planet?', 'Earth', 'Mars', 'Venus', 'Jupiter', 2, 'Mars is often called the Red Planet due to its reddish appearance.', 'Science'),
(80, 'Who was the British Prime Minister during WWII?', 'Neville Chamberlain', 'Winston Churchill', 'Tony Blair', 'Margaret Thatcher', 2, 'Winston Churchill served during most of WWII.', 'History'),
(81, 'Which war was fought between North and South America?', 'Civil War', 'World War I', 'Vietnam War', 'Korean War', 1, 'The American Civil War was fought between the North and the South.', 'History'),
(82, 'Which continent is the Sahara Desert located in?', 'Asia', 'Australia', 'Africa', 'South America', 3, 'The Sahara Desert is located in northern Africa.', 'Geography'),
(83, 'What is 7 + 6?', '11', '12', '13', '14', 3, '7 plus 6 equals 13.', 'Math');

-- --------------------------------------------------------

--
-- Table structure for table `user_inventory`
--

CREATE TABLE `user_inventory` (
  `user_id` int(11) NOT NULL,
  `item_id` int(11) NOT NULL,
  `is_selected` tinyint(1) DEFAULT 0,
  `obtained_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `user_inventory`
--

INSERT INTO `user_inventory` (`user_id`, `item_id`, `is_selected`, `obtained_at`) VALUES
(2, 12, 1, '2025-05-12 14:56:19'),
(2, 13, 0, '2025-05-12 14:58:17'),
(2, 14, 0, '2025-05-12 14:58:25'),
(2, 15, 0, '2025-05-12 15:35:42'),
(2, 16, 0, '2025-05-12 14:58:21'),
(2, 17, 0, '2025-05-12 14:58:12'),
(2, 18, 0, '2025-05-12 14:57:38'),
(2, 19, 0, '2025-05-12 15:36:02'),
(2, 20, 0, '2025-05-12 15:36:53'),
(2, 21, 0, '2025-05-12 15:35:58'),
(5, 18, 1, '2025-05-16 10:18:44'),
(6, 13, 1, '2025-05-29 11:35:17');

-- --------------------------------------------------------

--
-- Table structure for table `user_progress`
--

CREATE TABLE `user_progress` (
  `id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `question_id` int(11) NOT NULL,
  `is_correct` tinyint(1) NOT NULL,
  `response_time_ms` int(11) NOT NULL,
  `calculated_score` float NOT NULL,
  `timestamp` datetime DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `user_progress`
--

INSERT INTO `user_progress` (`id`, `user_id`, `question_id`, `is_correct`, `response_time_ms`, `calculated_score`, `timestamp`) VALUES
(5, 2, 2, 1, 3503, 0, '2025-04-06 18:06:31'),
(6, 2, 3, 2, 2251, 99, '2025-04-06 18:12:52'),
(8, 2, 1, 2, 23989, 78, '2025-04-10 10:40:23'),
(9, 5, 1, 2, 3823, 98, '2025-04-30 16:06:38'),
(10, 2, 5, 1, 10213, 0, '2025-05-10 17:45:15'),
(11, 2, 52, 2, 18401, 91, '2025-05-10 17:46:16'),
(12, 2, 13, 2, 4935, 100, '2025-05-10 17:46:41'),
(13, 2, 60, 1, 8879, 0, '2025-05-10 17:46:53'),
(14, 2, 77, 1, 3175, 0, '2025-05-10 17:46:58'),
(15, 2, 62, 1, 7103, 0, '2025-05-10 17:47:06'),
(16, 2, 16, 2, 6073, 100, '2025-05-10 17:47:14'),
(17, 2, 9, 1, 5390, 0, '2025-05-10 17:56:54'),
(18, 2, 73, 1, 7261, 0, '2025-05-10 17:57:10'),
(19, 2, 4, 2, 3829, 100, '2025-05-10 18:00:05'),
(20, 2, 6, 2, 5519, 100, '2025-05-10 18:13:27'),
(22, 2, 11, 1, 10079, 0, '2025-05-10 18:18:32'),
(23, 2, 82, 2, 9247, 100, '2025-05-10 18:18:54'),
(24, 5, 3, 2, 2789, 100, '2025-05-16 13:18:25'),
(25, 5, 2, 2, 2960, 100, '2025-05-16 14:22:46'),
(26, 5, 61, 2, 1977, 100, '2025-05-16 14:22:52'),
(27, 5, 39, 1, 2024, 0, '2025-05-16 14:22:56'),
(28, 2, 8, 1, 4547, 0, '2025-05-16 14:40:12'),
(29, 2, 21, 2, 3629, 100, '2025-05-16 14:40:18'),
(30, 2, 10, 1, 7994, 0, '2025-05-16 14:41:23'),
(31, 2, 57, 1, 5970, 0, '2025-05-16 14:41:32'),
(32, 2, 45, 1, 3152, 0, '2025-05-16 14:41:37'),
(33, 2, 55, 1, 2889, 0, '2025-05-16 14:41:42'),
(34, 2, 56, 1, 5531, 0, '2025-05-16 14:41:49'),
(35, 2, 78, 1, 4542, 0, '2025-05-16 14:41:56'),
(36, 2, 54, 2, 2944, 100, '2025-05-16 14:42:00'),
(37, 2, 7, 2, 13104, 96, '2025-05-24 14:30:46'),
(38, 2, 30, 1, 2602, 0, '2025-05-24 14:30:53'),
(39, 6, 1, 2, 4817, 100, '2025-05-29 14:35:03');

-- --------------------------------------------------------

--
-- Stand-in structure for view `user_rankings`
-- (See below for the actual view)
--
CREATE TABLE `user_rankings` (
`id` int(11)
,`name` varchar(100)
,`total_score` double
,`total_time` decimal(32,0)
,`rank` bigint(21)
);

-- --------------------------------------------------------

--
-- Structure for view `user_rankings`
--
DROP TABLE IF EXISTS `user_rankings`;

CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `user_rankings`  AS SELECT `u`.`id` AS `id`, `u`.`name` AS `name`, sum(`up`.`calculated_score`) AS `total_score`, sum(`up`.`response_time_ms`) AS `total_time`, rank() over ( order by sum(`up`.`calculated_score`) desc,sum(`up`.`response_time_ms`)) AS `rank` FROM ((`accounts` `u` join `user_progress` `up` on(`u`.`id` = `up`.`user_id`)) join `questions` `q` on(`up`.`question_id` = `q`.`id`)) GROUP BY `u`.`id` ;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `accounts`
--
ALTER TABLE `accounts`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `email` (`email`);

--
-- Indexes for table `chests`
--
ALTER TABLE `chests`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `profile_items`
--
ALTER TABLE `profile_items`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idx_profile_items_rarity` (`rarity`);

--
-- Indexes for table `questions`
--
ALTER TABLE `questions`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `user_inventory`
--
ALTER TABLE `user_inventory`
  ADD UNIQUE KEY `user_id` (`user_id`,`item_id`) COMMENT 'Prevent duplicate items per user',
  ADD KEY `item_id` (`item_id`),
  ADD KEY `idx_user_inventory_user` (`user_id`),
  ADD KEY `idx_user_inventory_selected` (`user_id`,`is_selected`);

--
-- Indexes for table `user_progress`
--
ALTER TABLE `user_progress`
  ADD PRIMARY KEY (`id`),
  ADD KEY `user_id` (`user_id`),
  ADD KEY `question_id` (`question_id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `accounts`
--
ALTER TABLE `accounts`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT for table `chests`
--
ALTER TABLE `chests`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `profile_items`
--
ALTER TABLE `profile_items`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=23;

--
-- AUTO_INCREMENT for table `questions`
--
ALTER TABLE `questions`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=84;

--
-- AUTO_INCREMENT for table `user_progress`
--
ALTER TABLE `user_progress`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=40;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `user_inventory`
--
ALTER TABLE `user_inventory`
  ADD CONSTRAINT `user_inventory_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `accounts` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `user_inventory_ibfk_2` FOREIGN KEY (`item_id`) REFERENCES `profile_items` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `user_progress`
--
ALTER TABLE `user_progress`
  ADD CONSTRAINT `user_progress_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `accounts` (`id`),
  ADD CONSTRAINT `user_progress_ibfk_2` FOREIGN KEY (`question_id`) REFERENCES `questions` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
