# frozen_string_literal: true

module SeleniumRake
  class Browsers
    BROWSERS = {
      'ff' => {
        driver: 'Marionette',
      },
      'marionette' => {
        driver: 'Marionette',
      },
      'ie' => {
        driver: 'Ie',
      },
      'edge' => {
        driver: 'Edge',
      },
      'chrome' => {
        driver: 'Chrome',
      },
      'chromiumedge' => {
        driver: 'ChromiumEdge',
      },
      'remote_firefox' => {
        driver: 'Remote',
        deps: [
          :remote_client,
          :'automationAgent-server-standalone',
          '//java/server/test/org/smartqa/automationagent/remote/server/auth:server'
        ]
      },
      'safari' => {
        driver: 'Safari',
      }
    }.freeze
  end
end
