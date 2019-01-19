user.data.file <- '../dataByUser.csv'
site.data.file <- '../dataBySite.csv'

# Purpose: Reads data from dataByUser.csv into a data frame.
# Output: df, dataframe containing user data.
ReadDataByUser <- function() {
    df <- read.csv(file = user.data.file, header = TRUE, sep = ',')
    return(df)
}

# Purpose: Reads data from dataBySite.csv into a data frame.
# Output: df, dataframe containing user data.
ReadDataBySite <- function() {
    df <- read.csv(file = site.data.file, header = FALSE, sep = ',')
    return(df)
}

# Purpose: Constructs a new data frame consisting of only elements associated 
#          with a scheme.
# input:   df, dataframe containing user data.
# input:   scheme, title string of desired schme.
# output:  new data frame containing only user data associated with scheme.
BuildSchemeTable <- function(df, scheme) {
    # Build new subset of df containing only elements associated with scheme
    scheme.df <- subset(df, df$Scheme == scheme)
    scheme.df$Scheme <- NULL
    return(scheme.df)
}

# Purpose: Constructs a dataframe of descriptive statistics for user data.
# input:   df, dataframe of user data.
# output:  dataframe containing descriptive statistics for each user.
BuildUserStatsTable <- function(df) {
    stats.df <- df[,c('uID', 'Scheme')]

    #convert string to character sequence
    df$TotalTestTimes <- as.character(df$TotalTestTimes)
    df$SuccessfulTestTimes <- as.character(df$SuccessfulTestTimes)
    df$UnsuccessfulTestTimes <- as.character(df$UnsuccessfulTestTimes)

    #compute mean, med, and mode for each set of data
    #create new table entries for each set of data
    stats.df$TotalTestTimes.mn <- mapply(function(x)mean(as.numeric(x)), strsplit(df$TotalTestTimes, ','))
    stats.df$TotalTestTimes.med <- mapply(function(x)median(as.numeric(x)), strsplit(df$TotalTestTimes, ','))
    stats.df$TotalTestTimes.sd <- mapply(function(x)sd(as.numeric(x)), strsplit(df$TotalTestTimes, ','))
    stats.df$SuccTestTimes.mn <- mapply(function(x)mean(as.numeric(x)), strsplit(df$SuccessfulTestTimes, ','))
    stats.df$SuccTestTimes.med <- mapply(function(x)median(as.numeric(x)), strsplit(df$SuccessfulTestTimes, ','))
    stats.df$SuccTestTimes.sd <- mapply(function(x)sd(as.numeric(x)), strsplit(df$SuccessfulTestTimes, ','))
    stats.df$UnsuccTestTimes.mn <- mapply(function(x)mean(as.numeric(x)), strsplit(df$UnsuccessfulTestTimes, ','))
    stats.df$UnsuccTestTimes.med <- mapply(function(x)median(as.numeric(x)), strsplit(df$UnsuccessfulTestTimes, ','))
    stats.df$UnsuccTestTimes.sd <- mapply(function(x)sd(as.numeric(x)), strsplit(df$UnsuccessfulTestTimes, ','))

    return(stats.df)
}

# Purpose: Constructs a dataframe of descriptive statistics for each scheme.
# input:   df, dataframe containing user data.
# output:  dataframe containing descriptive statistics.
BuildSchemeStatsTable <- function(df) {
    #Construct new dataframe containing statistics for a given scheme df
    stats.df <- data.frame(
        #Get stats for logins
        TotalLogs.mn = mean(df$TotalLogs),
        TotalLogs.med = median(df$TotalLogs),
        TotalLogs.sd = sd(df$TotalLogs),
        SuccLogs.mn = mean(df$SuccLogs),
        SuccLogs.med = median(df$SuccLogs),
        SuccLogs.sd = sd(df$SuccLogs),
        FailLogs.mn = mean(df$FailLogs),
        FailLogs.med = median(df$FailLogs),
        FailLogs.sd = sd(df$FailLogs),

        #Get stats for login times
        TotalLogTime.mn = mean(df$TotalLogTime),
        TotalLogTime.med = median(df$TotalLogTime),
        TotalLogTime.sd = sd(df$TotalLogTime),
        SuccLogTime.mn = mean(df$SuccLogTime),
        SuccLogTime.med = median(df$SuccLogs),
        SuccLogTime.sd = sd(df$SuccLogTime),
        FailLogTime.mn = mean(df$FailLogTime),
        FailLogTime.med = median(df$FailLogTime),
        FailLogTime.sd = sd(df$FailLogTime),

        #Get stats for passwords created and practices
        PassCreated.mn = mean(df$PasswordsCreated),
        PassCreated.med = median(df$PasswordsCreated),
        PassCreated.sd = sd(df$PasswordsCreated),
        NumPractices.mn = mean(df$NumPractices),
        NumPractices.med = median(df$NumPractices),
        NumPractices.sd = sd(df$NumPractices),
        stringsAsFactors = FALSE
    )

    return(stats.df)
}

# Purpose: Produces a summary of descriptive statstics for dataframe.
# input:   df, dataframe containing user data.
# output:  descriptive stats with respect to a scheme
BuildSchemeDataSummary <- function(df) {
    df$uID <- NULL
    desc.stats <- aggregate(
        . ~ Scheme, 
        data = df, 
        FUN = function(x)c(mn = mean(x), med = median(x), sd = sd(x))
    )
    return(desc.stats)
}

# Purpose: Produces a summary of descriptive statstics for dataframe.
# input:   df, dataframe containing user data.
# output:  descriptive stats with respect to users
BuildUserDataSummary <- function(df) {
    df <- df[,c('uID', 'Scheme', 'TotalTestTimes', 'SuccessfulTestTimes', 'UnsuccessfulTestTimes')]
    desc.stats <- aggregate(
        . ~ uID+Scheme, 
        data = df, 
        FUN = function(x)c(
            mn = mapply(function(x)mean(as.numeric(x)), strsplit(as.character(x), ',')), 
            med = mapply(function(x)median(as.numeric(x)), strsplit(as.character(x), ',')), 
            sd = mapply(function(x)sd(as.numeric(x), na.rm=T), strsplit(as.character(x), ','))
        )
    )
    return(desc.stats)
}

# Purpose: Build Histograms for user login times and writes them to png.
# input:   df, dataframe of user data
# input:   title, title string for histogram
BuildHistogramUserLoginTimes <- function(df, title) {
    # Graph totalLogs
    htitle <- paste('User Total Login Time', title, sep = ' ', collapse = ' ')
    ftitle <- paste(htitle, 'Total Login Time.png', sep = ' ', collapse = ' ')
    png(ftitle)
    df$TotalLogTime <- ifelse(df$TotalLogTime >= 250000, NA, df$TotalLogTime)
    hist(df$TotalLogTime,
        main = htitle,
        xlab = 'Total Login Time'
    )
    dev.off()

    # Graph succlogs
    htitle <- paste('User Successful Login Time', title, sep = ' ', collapse = ' ')
    ftitle <- paste(htitle, 'Total Succ Login Time.png', sep = ' ', collapse = ' ')
    png(ftitle)
    df$SuccLogTime <- ifelse(df$SuccLogTime >= 250000, NA, df$SuccLogTime)
    hist(df$SuccLogTime,
        main = htitle,
        xlab = 'Successful Login Time'
    )
    dev.off()

    # Graph faillogs
    htitle <- paste('User Failed Login Time', title, sep = ' ', collapse = ' ')
    ftitle <- paste(htitle, 'Total Fail Login Time.png', sep = ' ', collapse = ' ')
    png(ftitle)
    df$FailLogTime <- ifelse(df$FailLogTime >= 250000, NA, df$FailLogTime)
    hist(df$FailLogTime,
        main = htitle,
        xlab = 'Failed Login Time'
    )
    dev.off()
}

# Purpose: Build Histograms for user logins and writes them to png.
# input:   df, dataframe of user data
# input:   title, title string for histogram
BuildHistogramUserLogins <- function(df, title) {
    # Graph totalLogs
    htitle <- paste('User Total Logins', title, sep = ' ', collapse = ' ')
    ftitle <- paste(htitle, 'Total Logins.png', sep = ' ', collapse = ' ')
    png(ftitle)
    hist(
        df$TotalLogs,
        main = htitle,
        xlab = 'Number of Total Logins'
    )
    dev.off()

    # Graph succlogs
    htitle <- paste('User Successful Logins', title, sep = ' ', collapse = ' ')
    ftitle <- paste(htitle, 'Total Succ Logins.png', sep = ' ', collapse = ' ')
    png(ftitle)
    hist(
        df$SuccLogs,
        main = htitle,
        xlab = 'Number of Successful Logins'
    )
    dev.off()

    # Graph succlogs
    htitle <- paste('User Failed Logins', title, sep = ' ', collapse = ' ')
    ftitle <- paste(htitle, 'Total Fail Logins.png', sep = ' ', collapse = ' ')
    png(ftitle)
    hist(
        df$FailLogs,
        main = htitle,
        xlab = 'Number of Failed Logins'
    )
    dev.off()
}

# Purpose: Constructs boxplots of user login times by scheme and write them to png.
# input:   df, dataframe of user data
BuildBoxPlotUserLoginTimes <- function(df) {
    # Construct boxplot of total login time
    png('Boxplot Total Login Time.png')
    df$TotalLogTime <- ifelse(df$TotalLogTime >= 50000, NA, df$TotalLogTime)
    boxplot(
        main = 'Boxplot User Total Login Time by Scheme',
        df$TotalLogTime ~ df$Scheme, 
        df,
        ylab = 'Total Login Time',
        xlab = 'Scheme'
    )
    dev.off()

    # Construct boxplot of total successful login time
    png('Boxplot Succ Login Time.png')
    df$SuccLogTime <- ifelse(df$SuccLogTime >= 50000, NA, df$SuccLogTime)
    boxplot(
        main = 'Boxplot User Successful Login Time by Scheme',
        df$SuccLogTime ~ df$Scheme, 
        df,
        ylab = 'Successful Login Time',
        xlab = 'Scheme'
    )
    dev.off()

    # Construct boxplot of total fail login time
    png('Boxplot Fail Login Time.png')
    df$FailLogTime <- ifelse(df$FailLogTime >= 50000, NA, df$FailLogTime)
    boxplot(
        main = 'Boxplot User Failed Login Time by Scheme',
        df$FailLogTime ~ df$Scheme, 
        df,
        ylab = 'Failed Login Time',
        xlab = 'Scheme'
    )
    dev.off()
}

# Purpose: Constructs boxplots of user login times by user and write them to png.
# input:   df, dataframe of user data
BuildBoxPlotPerUserLoginTimes <- function(df) {
    # Construct boxplot of total login time
    png('Boxplot Total User Login Time.png')
    df$TotalTestTimes <- as.character(df$TotalTestTimes)
    boxplot(
        main = 'Boxplot User Total Login Time',
        lapply(
            strsplit(df$TotalTestTimes, ','), 
            FUN = function(x) ifelse(as.numeric(x) >= 500, NA, as.numeric(x))
        ),
        xlab = 'User ID',
        ylab = 'Total Login Time',
        names=df$uID
    )
    dev.off()

    # Construct boxplot of total successful login time
    png('Boxplot User Succ Login Time.png')
    df$SuccessfulTestTimes <- as.character(df$SuccessfulTestTimes)
    boxplot(
        main = 'Boxplot User Successful Login Time',
        lapply(
            strsplit(df$SuccessfulTestTimes, ','), 
            FUN = function(x) ifelse(as.numeric(x) >= 500, NA, as.numeric(x))
        ),
        xlab = 'User ID',
        ylab = 'Total Successful Login Time',
        names=df$uID
    )
    dev.off()

    # Construct boxplot of total fail login time
    png('Boxplot User Fail Login Time.png')
    df$UnsuccessfulTestTimes <- as.character(df$UnsuccessfulTestTimes)
    boxplot(
        main = 'Boxplot User Failed Login Time',
        lapply(
            strsplit(df$UnsuccessfulTestTimes, ','), 
            FUN = function(x) ifelse(as.numeric(x) >= 500, NA, as.numeric(x))
        ),
        xlab = 'User ID',
        ylab = 'Total Failed Login Time',
        names=df$uID
    )
    dev.off()
}

# Required for statistical analysis:
#   1. mean, std dev, median num logins per user (total, succ., & fail)
#   2. mean, std dev, median login time per user (total, succ, fail.)
#   3. histograms & boxplots: login time per user
#   4. histograms only: num logins
#   5. More graphs if required

# Primary entry point SchemeComparison
Main <- function() {
    #Read data
    user.data <- ReadDataByUser()
    site.data <- ReadDataBySite()

    #Build tables for each scheme
    testtextrandom.table <- BuildSchemeTable(user.data, 'testtextrandom')
    testpasstiles.table <- BuildSchemeTable(user.data, 'testpasstiles')

    #Build tables for descriptive statistics for each scheme
    testtextrandom.stats.table <- BuildSchemeStatsTable(testtextrandom.table)
    testpasstiles.stats.table <- BuildSchemeStatsTable(testpasstiles.table)

    #Build table for users stats
    user.stats <- BuildUserStatsTable(user.data)
    scheme.stats <- BuildSchemeStatsTable(user.data)

    #user.stats <- BuildUserDataSummary(user.data)
    scheme.stats <- BuildSchemeDataSummary(user.data)

    #Output descriptive stats to console and to csv files
    print('{====================DESCRIPTIVE STATS====================}')
    print('====================1. User stats====================')
    print(user.stats)
    write.csv(user.stats, file = 'userStats.csv', row.names = FALSE)
    print('User stats done.')
    readline('next?')
    print('====================2. Scheme stats====================')
    print(scheme.stats)
    write.csv(scheme.stats, file = 'schemeStats.csv', row.names = FALSE)
    print('Scheme stats done.')
    readline('next?')
    print('')
    print('')

    #Construct histograms for user logins
    print('{====================USER LOGIN DATA====================}')
    BuildHistogramUserLogins(testtextrandom.table, 'testtextrandom')
    BuildHistogramUserLogins(testpasstiles.table, 'testpasstiles')
    print('User logins histograms done.')
    readline('next?')

    #Construct histograms for user login times
    print('{====================USER LOGIN TIMES DATA====================}')
    BuildHistogramUserLoginTimes(testtextrandom.table, 'testtextrandom')
    BuildHistogramUserLoginTimes(testpasstiles.table, 'testpasstiles')
    print('User login times histograms done.')

    #Construct boxplots for user login
    BuildBoxPlotUserLoginTimes(user.data)
    BuildBoxPlotPerUserLoginTimes(user.data)
    print('User login times boxplots done.')

    print('All tasks finished. Check directory for graphs.')
    readline('Exit?')
}

Main()