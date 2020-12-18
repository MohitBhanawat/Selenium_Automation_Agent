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

require File.expand_path('../spec_helper', __dir__)

module Selenium
  module WebDriver
    module EdgeHtml
      describe Driver do
        let(:service) { instance_double(Service, launch: service_manager) }
        let(:service_manager) { instance_double(ServiceManager, uri: 'http://example.com') }
        let(:valid_response) do
          {status: 200,
           body: {value: {sessionId: 0, capabilities: Remote::Capabilities.edge}}.to_json,
           headers: {"content_type": "application/json"}}
        end

        def expect_request(body: nil, endpoint: nil)
          body = (body || {capabilities: {firstMatch: [browserName: "MicrosoftEdge",
                                                       platformName: "windows"]}}).to_json
          endpoint ||= "#{service_manager.uri}/session"
          stub_request(:post, endpoint).with(body: body).to_return(valid_response)
        end

        before do
          allow(Service).to receive_messages(new: service, executable_path: nil)
        end

        it 'does not require any parameters' do
          expect_request

          expect { Driver.new }.not_to raise_exception
        end

        context 'with :desired capabilities' do
          it 'accepts value as a Symbol' do
            expect_request(body: {capabilities: {firstMatch: [browserName: "MicrosoftEdge",
                                                              platformName: 'windows']}})
            expect {
              expect { Driver.new(desired_capabilities: :edge) }.to have_deprecated(:desired_capabilities)
            }.not_to raise_exception
          end

          it 'accepts Capabilities.edge_html' do
            capabilities = Remote::Capabilities.edge_html(invalid: 'foobar')
            expect_request(body: {capabilities: {firstMatch: [browserName: "MicrosoftEdge",
                                                              platformName: 'windows',
                                                              invalid: 'foobar']}})

            expect {
              expect { Driver.new(desired_capabilities: capabilities) }.to have_deprecated(:desired_capabilities)
            }.not_to raise_exception
          end

          it 'accepts constructed Capabilities with Snake Case as Symbols' do
            capabilities = Remote::Capabilities.new(browser_name: 'MicrosoftEdge', invalid: 'foobar')
            expect_request(body: {capabilities: {firstMatch: [browserName: "MicrosoftEdge",
                                                              invalid: 'foobar']}})

            expect {
              expect { Driver.new(desired_capabilities: capabilities) }.to have_deprecated(:desired_capabilities)
            }.not_to raise_exception
          end

          it 'accepts constructed Capabilities with Camel Case as Symbols' do
            capabilities = Remote::Capabilities.new(browserName: 'MicrosoftEdge', invalid: 'foobar')
            expect_request(body: {capabilities: {firstMatch: [browserName: "MicrosoftEdge",
                                                              invalid: 'foobar']}})

            expect {
              expect { Driver.new(desired_capabilities: capabilities) }.to have_deprecated(:desired_capabilities)
            }.not_to raise_exception
          end

          it 'accepts constructed Capabilities with Camel Case as Strings' do
            capabilities = Remote::Capabilities.new('browserName' => 'MicrosoftEdge', 'invalid' => 'foobar')
            expect_request(body: {capabilities: {firstMatch: [browserName: "MicrosoftEdge", invalid: 'foobar']}})

            expect {
              expect { Driver.new(desired_capabilities: capabilities) }.to have_deprecated(:desired_capabilities)
            }.not_to raise_exception
          end

          it 'accepts Hash with Camel Case keys as Symbols' do
            capabilities = {browserName: 'MicrosoftEdge', invalid: 'foobar'}
            expect_request(body: {capabilities: {firstMatch: [browserName: "MicrosoftEdge", invalid: 'foobar']}})

            expect {
              expect { Driver.new(desired_capabilities: capabilities) }.to have_deprecated(:desired_capabilities)
            }.not_to raise_exception
          end

          it 'accepts Hash with Camel Case keys as Strings' do
            capabilities = {"browserName" => 'MicrosoftEdge', "invalid" => 'foobar'}
            expect_request(body: {capabilities: {firstMatch: [browserName: "MicrosoftEdge", invalid: 'foobar']}})

            expect {
              expect { Driver.new(desired_capabilities: capabilities) }.to have_deprecated(:desired_capabilities)
            }.not_to raise_exception
          end
        end

        it 'accepts provided Options as sole parameter' do
          opts = {start_page: 'http://automationAgent.dev'}
          expect_request(body: {capabilities: {firstMatch: [browserName: "MicrosoftEdge",
                                                            "ms:startPage": "http://automationAgent.dev"]}})

          expect {
            expect { Driver.new(options: Options.new(**opts)) }.to have_deprecated(:browser_options)
          }.not_to raise_exception
        end

        it 'accepts combination of Options and Capabilities' do
          caps = Remote::Capabilities.edge_html(invalid: 'foobar')
          browser_opts = {start_page: 'http://automationAgent.dev'}
          expect_request(body: {capabilities: {firstMatch: [browserName: "MicrosoftEdge",
                                                            platformName: "windows",
                                                            invalid: "foobar",
                                                            "ms:startPage": "http://automationAgent.dev"]}})

          expect {
            expect {
              Driver.new(options: Options.new(**browser_opts), desired_capabilities: caps)
            }.to have_deprecated(%i[browser_options desired_capabilities])
          }.not_to raise_exception
        end

        it 'raises an ArgumentError if parameter is not recognized' do
          msg = 'Unable to create a driver with parameters: {:invalid=>"foo"}'
          expect { Driver.new(invalid: 'foo') }.to raise_error(ArgumentError, msg)
        end

        context 'with :capabilities' do
          it 'accepts value as a Symbol' do
            expect_request(body: {capabilities: {firstMatch: [browserName: "MicrosoftEdge",
                                                              platformName: 'windows']}})
            expect { Driver.new(capabilities: :edge_html) }.not_to raise_exception
          end

          it 'accepts Capabilities.edge_html' do
            capabilities = Remote::Capabilities.edge_html(invalid: 'foobar')
            expect_request(body: {capabilities: {firstMatch: [browserName: "MicrosoftEdge",
                                                              platformName: 'windows',
                                                              invalid: 'foobar']}})

            expect { Driver.new(capabilities: capabilities) }.not_to raise_exception
          end

          it 'accepts constructed Capabilities with Snake Case as Symbols' do
            capabilities = Remote::Capabilities.new(browser_name: 'MicrosoftEdge', invalid: 'foobar')
            expect_request(body: {capabilities: {firstMatch: [browserName: "MicrosoftEdge", invalid: 'foobar']}})

            expect { Driver.new(capabilities: capabilities) }.not_to raise_exception
          end

          it 'accepts constructed Capabilities with Camel Case as Symbols' do
            capabilities = Remote::Capabilities.new(browserName: 'MicrosoftEdge', invalid: 'foobar')
            expect_request(body: {capabilities: {firstMatch: [browserName: "MicrosoftEdge", invalid: 'foobar']}})

            expect { Driver.new(capabilities: capabilities) }.not_to raise_exception
          end

          it 'accepts constructed Capabilities with Camel Case as Strings' do
            capabilities = Remote::Capabilities.new('browserName' => 'MicrosoftEdge', 'invalid' => 'foobar')
            expect_request(body: {capabilities: {firstMatch: [browserName: "MicrosoftEdge", invalid: 'foobar']}})

            expect { Driver.new(capabilities: capabilities) }.not_to raise_exception
          end

          it 'accepts Hash with Camel Case keys as Symbols but is deprecated' do
            capabilities = {browserName: 'MicrosoftEdge', invalid: 'foobar'}
            expect_request(body: {capabilities: {firstMatch: [browserName: "MicrosoftEdge", invalid: 'foobar']}})

            expect {
              expect { Driver.new(capabilities: capabilities) }.to have_deprecated(:capabilities_hash)
            }.not_to raise_exception
          end

          it 'accepts Hash with Camel Case keys as Strings but is deprecated' do
            capabilities = {"browserName" => 'MicrosoftEdge', "invalid" => 'foobar'}
            expect_request(body: {capabilities: {firstMatch: [browserName: "MicrosoftEdge", invalid: 'foobar']}})

            expect {
              expect { Driver.new(capabilities: capabilities) }.to have_deprecated(:capabilities_hash)
            }.not_to raise_exception
          end

          context 'when value is an Array' do
            let(:as_json_object) do
              Class.new do
                def as_json(*)
                  {'company:key': 'value'}
                end
              end
            end

            it 'with Options instance' do
              browser_opts = {start_page: 'http://automationAgent.dev'}
              expect_request(body: {capabilities: {firstMatch: [browserName: "MicrosoftEdge",
                                                                'ms:startPage': 'http://automationAgent.dev']}})

              expect { Driver.new(capabilities: [Options.new(**browser_opts)]) }.not_to raise_exception
            end

            it 'with Capabilities instance' do
              capabilities = Remote::Capabilities.new(browser_name: 'MicrosoftEdge', invalid: 'foobar')
              expect_request(body: {capabilities: {firstMatch: [browserName: "MicrosoftEdge", invalid: 'foobar']}})

              expect { Driver.new(capabilities: [capabilities]) }.not_to raise_exception
            end

            it 'with Options instance and an instance of a custom object responding to #as_json' do
              expect_request(body: {capabilities: {firstMatch: [browserName: "MicrosoftEdge",
                                                                # 'ms:startPage': 'http://automationAgent.dev',
                                                                'company:key': 'value']}})
              expect { Driver.new(capabilities: [Options.new, as_json_object.new]) }.not_to raise_exception
            end

            it 'with Options instance, Capabilities instance and instance of a custom object responding to #as_json' do
              capabilities = Remote::Capabilities.new(browser_name: 'MicrosoftEdge', invalid: 'foobar')
              options = Options.new(start_page: 'http://automationAgent.dev')
              expect_request(body: {capabilities: {firstMatch: [browserName: "MicrosoftEdge",
                                                                invalid: 'foobar',
                                                                'ms:startPage': 'http://automationAgent.dev',
                                                                'company:key': 'value']}})

              expect { Driver.new(capabilities: [capabilities, options, as_json_object.new]) }.not_to raise_exception
            end
          end
        end
      end
    end # Edge
  end # WebDriver
end # Selenium
