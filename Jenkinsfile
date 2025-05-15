// 使用任何可用的 Jenkins 构建节点（agent），支持自动分配
pipeline {
    agent any

    // 可配置 Maven 参数（例如强制终止测试失败）
    environment {
        MAVEN_OPTS = "-Dmaven.test.failure.ignore=false"
    }

    stages {

        // 1️⃣ 拉取代码阶段
        stage('Checkout') {
            steps {
                echo "📦 拉取源码"
                // 修改为你项目实际的 Git 仓库地址和分支
                git url: 'https://github.com/saltedfish-lg/auto_test_framework.git', branch: 'main'
            }
        }

        // 2️⃣ 编译 + 测试阶段
        stage('Build & Test') {
            steps {
                echo "🔧 编译项目并运行 Web 自动化测试"
                // 使用 testng-local.xml 作为入口运行 Web 自动化测试
                sh 'mvn clean test -DsuiteXmlFile=testng.xml'
            }
        }

        // 3️⃣ 生成 Allure 报告
        stage('Generate Allure Report') {
            steps {
                echo "📊 生成 Allure 报告"
                // 调用 Allure 插件生成 HTML 报告到 target/allure-report
                sh 'mvn allure:report'
            }
        }

        // 4️⃣ 归档 Allure 报告供 Jenkins UI 显示
        stage('Archive Allure Report') {
            steps {
                echo "📦 归档 Allure 报告目录"
                // 供 Jenkins 插件使用
                allure includeProperties: false, jdk: '', results: [[path: 'target/allure-results']]
                // 将完整报告归档为构建产物
                archiveArtifacts artifacts: 'target/allure-report/**', fingerprint: true
            }
        }

        // 5️⃣ 邮件通知阶段（需提前配置 SMTP）
        stage('Send Email') {
            steps {
                echo "📧 发送测试报告邮件 + 附加失败截图"
                emailext (
                    subject: "📸 Baidu 自动化测试结果",
                    body: """Hi 团队，<br>
        <br>
        ✅ 自动化测试已执行完成。<br>
        请查阅 Allure 报告以及以下附件截图（如有）以分析失败用例。<br><br>
        🧪 <b>构建编号:</b> \$BUILD_NUMBER<br>
        📁 <b>报告位置:</b> <a href="\$BUILD_URL">构建详情</a><br><br>
        --<br>
        此邮件由 Jenkins 自动生成。
        """,
                    mimeType: 'text/html',
                    to: '1325707506@qq.com',
                    attachmentsPattern: 'target/screenshots/*.png'
                )
            }
        }


        // 6️⃣ 企业微信 / 钉钉通知（如果集成）
        stage('Notify WeChat / DingTalk') {
            steps {
                echo "📲 推送企业微信 / 钉钉通知（如启用）"
                // 运行你的 SendNotificationMain 类，自动读取 application.properties 发送通知
                // "|| true" 是为了防止即使通知失败也不影响整体构建
                sh 'java -cp target/classes com.baidu.notification.SendNotificationMain || true'
            }
        }
    }

    // 💡 构建结束后统一处理（无论成功或失败）
    post {
        always {
            echo "🧹 构建后操作：清理缓存 / 归档测试报告"
            // 解析 TestNG 测试结果 XML（JUnit 格式）
            junit '**/target/surefire-reports/*.xml'
        }

        success {
            echo "✅ 构建成功：所有阶段顺利完成"
        }

        failure {
            echo "❌ 构建失败：请检查错误日志与报告"
        }
    }
}
