pipeline {
    agent any

    environment {
        MAVEN_HOME = tool 'maven3.9.9'
        PATH = "${MAVEN_HOME}/bin:${env.PATH}"
        NOTIFY_CMD = 'java -cp target/classes com.baidu.notification.SendNotificationMain'
    }

    options {
        skipStagesAfterUnstable()
        ansiColor('xterm')
        buildDiscarder(logRotator(numToKeepStr: '10'))
    }

    stages {
        stage('Checkout') {
            steps {
                echo '📥 拉取项目代码'
                checkout scm
            }
        }

        stage('Build & Test') {
            steps {
                echo '🔧 编译项目并运行 Web 自动化测试'
                bat 'mvn clean test -DsuiteXmlFile=testng.xml -Dfile.encoding=UTF-8'
            }
        }

        stage('Generate Allure Report') {
            steps {
                echo '📊 生成 Allure 报告'
                allure includeProperties: false, jdk: '', results: [[path: 'target/allure-results']]
            }
        }

        stage('Archive Artifacts') {
            steps {
                echo '🧳 归档失败截图和报告数据'
                archiveArtifacts artifacts: 'target/screenshots/*.png', allowEmptyArchive: true
                archiveArtifacts artifacts: 'target/allure-results/**', allowEmptyArchive: true
            }
        }

        stage('Notify WeChat / DingTalk') {
            when {
                expression { return currentBuild.currentResult != 'ABORTED' }
            }
            steps {
                echo '📲 调用 Java 通知主程序'
                catchError(buildResult: 'UNSTABLE', stageResult: 'FAILURE') {
                    bat 'mvn compile -Dfile.encoding=UTF-8'
                    bat "dir target\\classes\\com\\baidu\\notification"
                    bat "java -cp target/classes -Dbuild.status=${currentBuild.currentResult} -Dreport.allure.link=http://your-server/allure-report com.baidu.notification.SendNotificationMain"
                }
            }
        }
    }

    post {
        always {
            echo '🧹 构建后操作：归档测试结果'
            junit 'target/surefire-reports/*.xml'
        }
        failure {
            echo '❌ 构建失败'
        }
    }
}