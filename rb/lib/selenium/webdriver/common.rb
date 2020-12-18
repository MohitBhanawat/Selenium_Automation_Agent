# frozen_string_literal: true

# Licensed to the Software Freedom Conservancy (SFC) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The SFC licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#   http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License.

require 'automationAgent/webdriver/common/error'
require 'automationAgent/webdriver/common/platform'
require 'automationAgent/webdriver/common/proxy'
require 'automationAgent/webdriver/common/log_entry'
require 'automationAgent/webdriver/common/file_reaper'
require 'automationAgent/webdriver/common/service'
require 'automationAgent/webdriver/common/service_manager'
require 'automationAgent/webdriver/common/socket_lock'
require 'automationAgent/webdriver/common/socket_poller'
require 'automationAgent/webdriver/common/port_prober'
require 'automationAgent/webdriver/common/zipper'
require 'automationAgent/webdriver/common/wait'
require 'automationAgent/webdriver/common/alert'
require 'automationAgent/webdriver/common/target_locator'
require 'automationAgent/webdriver/common/navigation'
require 'automationAgent/webdriver/common/timeouts'
require 'automationAgent/webdriver/common/window'
require 'automationAgent/webdriver/common/logger'
require 'automationAgent/webdriver/common/logs'
require 'automationAgent/webdriver/common/manager'
require 'automationAgent/webdriver/common/search_context'
require 'automationAgent/webdriver/common/interactions/key_actions'
require 'automationAgent/webdriver/common/interactions/pointer_actions'
require 'automationAgent/webdriver/common/interactions/interactions'
require 'automationAgent/webdriver/common/interactions/input_device'
require 'automationAgent/webdriver/common/interactions/interaction'
require 'automationAgent/webdriver/common/interactions/none_input'
require 'automationAgent/webdriver/common/interactions/key_input'
require 'automationAgent/webdriver/common/interactions/pointer_input'
require 'automationAgent/webdriver/common/action_builder'
require 'automationAgent/webdriver/common/html5/shared_web_storage'
require 'automationAgent/webdriver/common/html5/local_storage'
require 'automationAgent/webdriver/common/html5/session_storage'
require 'automationAgent/webdriver/common/driver_extensions/takes_screenshot'
require 'automationAgent/webdriver/common/driver_extensions/rotatable'
require 'automationAgent/webdriver/common/driver_extensions/has_web_storage'
require 'automationAgent/webdriver/common/driver_extensions/downloads_files'
require 'automationAgent/webdriver/common/driver_extensions/has_location'
require 'automationAgent/webdriver/common/driver_extensions/has_session_id'
require 'automationAgent/webdriver/common/driver_extensions/has_remote_status'
require 'automationAgent/webdriver/common/driver_extensions/has_network_conditions'
require 'automationAgent/webdriver/common/driver_extensions/has_network_connection'
require 'automationAgent/webdriver/common/driver_extensions/has_permissions'
require 'automationAgent/webdriver/common/driver_extensions/has_debugger'
require 'automationAgent/webdriver/common/driver_extensions/uploads_files'
require 'automationAgent/webdriver/common/driver_extensions/has_addons'
require 'automationAgent/webdriver/common/driver_extensions/has_devtools'
require 'automationAgent/webdriver/common/keys'
require 'automationAgent/webdriver/common/profile_helper'
require 'automationAgent/webdriver/common/options'
require 'automationAgent/webdriver/common/driver'
require 'automationAgent/webdriver/common/element'
