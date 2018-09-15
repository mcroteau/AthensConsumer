
--
-- Database: athens
--

-- --------------------------------------------------------

--
-- Table structure for table krnlog
--

CREATE TABLE krnlog (
  id SERIAL,
  kdate decimal(14,0) DEFAULT NULL,
  kstatus varchar(26) DEFAULT NULL,
  ktot decimal(24,0) DEFAULT NULL,
  kproc decimal(24,0) DEFAULT NULL,
  kadtcnt decimal(24,0) DEFAULT NULL,
  ktype varchar(27) DEFAULT NULL,
  kaudit text,
  PRIMARY KEY (id)
);

-- --------------------------------------------------------

--
-- Table structure for table krnwh
--

CREATE TABLE krnwh (
  id SERIAL,
  fpempn decimal(9,0) DEFAULT '0',
  fppunc decimal(14,0) DEFAULT '0',
  fptype varchar(1) DEFAULT '',
  fpclck varchar(15) DEFAULT '',
  fpbadg decimal(8,0) DEFAULT '0',
  fppcod decimal(15,3) DEFAULT '0.000',
  fstatus varchar(1) DEFAULT 'q',
  fpfkey varchar(15) DEFAULT '',
  krnlogid bigint DEFAULT '0',
  PRIMARY KEY (id)
); 

